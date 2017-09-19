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

import java.io.IOException;
import java.util.Set;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.stream.Collectors;


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
public class DivRankALSExample {

	public static void main(String[] args) throws IOException {
		String userPath = args[0];
		String itemPath = args[1];
		String featureDataIndexPath = args[2];
		String trainDataPath = args[3];
		String testDataPath = args[4];
		String featurePath = args[5];

		FastUserIndex<Long> userIndex = SimpleFastUserIndex.load(userPath, lp);
		FastItemIndex<Long> itemIndex = SimpleFastItemIndex.load(itemPath, lp);
		FastFeatureIndex<Long> featureIndex = SimpleFastFeatureIndex.load(featureDataIndexPath, lp);


		FastPreferenceData<Long, Long> trainData = SimpleFastPreferenceData.load(trainDataPath, lp, lp, ddp, userIndex, itemIndex);
		FastPreferenceData<Long, Long> testData = SimpleFastPreferenceData.load(testDataPath, lp, lp, ddp, userIndex, itemIndex);
		FeatureData<Long, String, Double> featureData = SimpleFeatureData.load(featurePath, lp, sp, dp);

		ItemDistanceModel<Long> dist = new CosineFeatureItemDistanceModel<>(featureData);

		//////////////////
		// RECOMMENDERS //
		//////////////////

		int k = 50;
		double lambda = 0.1;
		int numIter = 10;
		Factorization<Long, Long> divfactorization = new DivRankALSFactorizer<Long, Long>(lambda, dist,  numIter).factorize(k, trainData);
		Recommender<Long, Long> recommender = new MFRecommender<>(userIndex, itemIndex, divfactorization);



		////////////////////////////////
		// GENERATING RECOMMENDATIONS //
		////////////////////////////////
		Set<Long> targetUsers = testData.getUsersWithPreferences().collect(Collectors.toSet());
		RecommendationFormat<Long, Long> format = new SimpleRecommendationFormat<>(lp, lp);
		Function<Long, IntPredicate> filter = FastFilters.notInTrain(trainData);
		int maxLength = 100;
		RecommenderRunner<Long, Long> runner = new FastFilterRecommenderRunner<>(userIndex, itemIndex, targetUsers, format, filter, maxLength);
		System.out.println("Running Div RankALS");
		runner.run(recommender, "divrankals");

	}
}
