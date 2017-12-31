/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.insightcentre.ranksys.divmf;

import static es.uam.eps.ir.ranksys.core.util.parsing.DoubleParser.ddp;
import static es.uam.eps.ir.ranksys.core.util.parsing.Parsers.lp;
import static es.uam.eps.ir.ranksys.core.util.parsing.Parsers.sp;
import static es.uam.eps.ir.ranksys.core.util.parsing.Parsers.dp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.stream.Collectors;

import org.insightcentre.ranksys.divmf.DivRankALSFactorizerEILDBasedLambda.DiversityRegulariser;
import org.insightcentre.ranksys.novdiv.distance.CachedItemDistanceModel;

import es.uam.eps.ir.ranksys.core.feature.FeatureData;
import es.uam.eps.ir.ranksys.core.feature.SimpleFeatureData;
import es.uam.eps.ir.ranksys.core.format.RecommendationFormat;
import es.uam.eps.ir.ranksys.core.format.SimpleRecommendationFormat;
import es.uam.eps.ir.ranksys.fast.index.FastFeatureIndex;
import es.uam.eps.ir.ranksys.fast.index.FastItemIndex;
import es.uam.eps.ir.ranksys.fast.index.FastUserIndex;
import es.uam.eps.ir.ranksys.fast.index.SimpleFastFeatureIndex;
import es.uam.eps.ir.ranksys.fast.index.SimpleFastItemIndex;
import es.uam.eps.ir.ranksys.fast.index.SimpleFastUserIndex;
import es.uam.eps.ir.ranksys.fast.preference.FastPreferenceData;
import es.uam.eps.ir.ranksys.fast.preference.SimpleFastPreferenceData;
import es.uam.eps.ir.ranksys.mf.Factorization;
import es.uam.eps.ir.ranksys.mf.rec.MFRecommender;
import es.uam.eps.ir.ranksys.novdiv.distance.CosineFeatureItemDistanceModel;
import es.uam.eps.ir.ranksys.novdiv.distance.ItemDistanceModel;
import es.uam.eps.ir.ranksys.rec.Recommender;
import es.uam.eps.ir.ranksys.rec.runner.RecommenderRunner;
import es.uam.eps.ir.ranksys.rec.runner.fast.FastFilterRecommenderRunner;
import es.uam.eps.ir.ranksys.rec.runner.fast.FastFilters;

/**
 * Example main of recommendations.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 */
public class DivRankALSExampleEILDBasedLambda {
	public static void main(String[] args) throws IOException {
		String userPath = args[0];
		String itemPath = args[1];
		String trainDataPath = args[2];
		String testDataPath = args[3];
		String featurePath = args[4];


		FastUserIndex<Long> userIndex = SimpleFastUserIndex.load(userPath, lp);
		FastItemIndex<Long> itemIndex = SimpleFastItemIndex.load(itemPath, lp);
		FastPreferenceData<Long, Long> trainData = SimpleFastPreferenceData.load(trainDataPath, lp, lp, ddp, userIndex, itemIndex);
		FastPreferenceData<Long, Long> testData = SimpleFastPreferenceData.load(testDataPath, lp, lp, ddp, userIndex, itemIndex);
		//		FeatureData<Long, String, Double> featureData = SimpleFeatureData.load(featurePath, lp, sp, v -> 1.0);
		FeatureData<Long, String, Double> featureData = SimpleFeatureData.load(featurePath, lp, sp, dp);

		List<Double> lambdas = new ArrayList<Double> () ;
		lambdas.add(0.5);
		lambdas.add(-0.5);

		List<Boolean>itemImportanceWeightingArray = new ArrayList<Boolean> ();
		itemImportanceWeightingArray.add(true);
		itemImportanceWeightingArray.add(false);

		List<Boolean>useSimilarityArray = new ArrayList<Boolean>();
		useSimilarityArray.add(true);
		useSimilarityArray.add(false);

		List<DiversityRegulariser> regularisers = new ArrayList<DiversityRegulariser>();
		regularisers.add(DiversityRegulariser.NONE);
		regularisers.add(DiversityRegulariser.DQ);
		regularisers.add(DiversityRegulariser.LAPLACIAN_DQ);
		regularisers.add(DiversityRegulariser.P_LAPLACIAN_DQ);

		for(double lambdaD: lambdas){
			for(boolean itemImportanceWeighting: itemImportanceWeightingArray){
				for(DiversityRegulariser regulariser: regularisers){
					for(boolean useSimilarity: useSimilarityArray){
						int k = 20;
						int numIter = 10;
						Map<Long,Double>userEILDMap = new LinkedHashMap<Long,Double>();

						try (BufferedReader br = new BufferedReader(new FileReader(new File("/data/sidana/diversity/user_based_diversity/purch/eild_per_user")))) {
							String line;
							while ((line = br.readLine()) != null) {
								String [] array = line.split("\t");
								userEILDMap.put(Long.parseLong(array[0]), Double.parseDouble(array[1]));
							}
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						ItemDistanceModel<Long> dist = new CachedItemDistanceModel<>(new CosineFeatureItemDistanceModel<>(featureData), itemIndex);
						//						Factorization<Long, Long> factorization = new DivRankALSFactorizer<Long, Long>(lambdaD, dist, numIter, itemImportanceWeighting, regulariser, useSimilarity).factorize(k, trainData);
						Factorization<Long, Long> factorization = new DivRankALSFactorizerEILDBasedLambda<Long, Long>(lambdaD, dist, numIter, itemImportanceWeighting, regulariser, useSimilarity, userEILDMap).factorize(k, trainData);

						Recommender<Long, Long> recommender = new MFRecommender<>(userIndex, itemIndex, factorization);

						Set<Long> targetUsers = testData.getUsersWithPreferences().collect(Collectors.toSet());
						RecommendationFormat<Long, Long> format = new SimpleRecommendationFormat<>(lp, lp);
						Function<Long, IntPredicate> filter = FastFilters.notInTrain(trainData);
						int maxLength = 20;
						RecommenderRunner<Long, Long> runner = new FastFilterRecommenderRunner<>(userIndex, itemIndex, targetUsers, format, filter, maxLength);
						System.out.println("Running");
						runner.run(recommender, "divrankals_"+lambdaD+"_"+itemImportanceWeighting+"_"+regulariser+"_"+useSimilarity);
					}
				}
			}
		}

	}
}