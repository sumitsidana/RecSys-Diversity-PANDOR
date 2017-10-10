cd /data/sidana/diversity/user_based_diversity/ml20m/code/diversity

java -Xmx100G -cp RankSys-DivMF-0.1-jar-with-dependencies.jar org.insightcentre.ranksys.divmf.DivRankALSExampleParamTune /data/sidana/diversity/user_based_diversity/ml20m/diversity/unique_users /data/sidana/diversity/user_based_diversity/ml20m/diversity/unique_items /data/sidana/diversity/user_based_diversity/ml20m/diversity/train.csv /data/sidana/diversity/user_based_diversity/ml20m/diversity/test.csv /data/sidana/diversity/user_based_diversity/ml20m/diversity/features.ml20m

for type in divrankals_0.5_true_NONE_true divrankals_0.5_true_NONE_false divrankals_0.5_true_DQ_true divrankals_0.5_true_DQ_false divrankals_0.5_true_LAPLACIAN_DQ_true divrankals_0.5_true_LAPLACIAN_DQ_false divrankals_0.5_true_P_LAPLACIAN_DQ_true divrankals_0.5_true_P_LAPLACIAN_DQ_false divrankals_0.5_false_NONE_true divrankals_0.5_false_NONE_false divrankals_0.5_false_DQ_true divrankals_0.5_false_DQ_false divrankals_0.5_false_LAPLACIAN_DQ_true divrankals_0.5_false_LAPLACIAN_DQ_false divrankals_0.5_false_P_LAPLACIAN_DQ_true divrankals_0.5_false_P_LAPLACIAN_DQ_false divrankals_-0.5_true_NONE_true divrankals_-0.5_true_NONE_false divrankals_-0.5_true_DQ_true divrankals_-0.5_true_DQ_false divrankals_-0.5_true_LAPLACIAN_DQ_true divrankals_-0.5_true_LAPLACIAN_DQ_false divrankals_-0.5_true_P_LAPLACIAN_DQ_true divrankals_-0.5_true_P_LAPLACIAN_DQ_false divrankals_-0.5_false_NONE_true divrankals_-0.5_false_NONE_false divrankals_-0.5_false_DQ_true divrankals_-0.5_false_DQ_false divrankals_-0.5_false_LAPLACIAN_DQ_true divrankals_-0.5_false_LAPLACIAN_DQ_false divrankals_-0.5_false_P_LAPLACIAN_DQ_true divrankals_-0.5_false_P_LAPLACIAN_DQ_false

do
echo -e "$type" >> /data/sidana/diversity/user_based_diversity/ml20m/diversity/diversity_results
java -Xmx100G -cp RankSys-metrics-0.4.2-SNAPSHOT-jar-with-dependencies.jar:RankSys-core-0.4.2-SNAPSHOT-jar-with-dependencies.jar:RankSys-diversity-0.4.2-SNAPSHOT-jar-with-dependencies.jar:RankSys-novdiv-0.4.2-SNAPSHOT-jar-with-dependencies.jar:RankSys-novelty-0.4.2-SNAPSHOT-jar-with-dependencies.jar:RankSys-examples-0.4.2-SNAPSHOT-jar-with-dependencies.jar:RankSys-formats-0.4.2-SNAPSHOT-jar-with-dependencies.jar es.uam.eps.ir.ranksys.examples.MetricExample /data/sidana/diversity/user_based_diversity/ml20m/diversity/train.csv /data/sidana/diversity/user_based_diversity/ml20m/diversity/test.csv /data/sidana/diversity/user_based_diversity/ml20m/diversity/features.ml20m $type  1.0 >> /data/sidana/diversity/user_based_diversity/ml20m/diversity/diversity_results
done


cd /data/sidana/diversity/user_based_diversity/ml20m/code/not_diversity

java -Xmx100G -cp RankSys-DivMF-0.1-jar-with-dependencies.jar org.insightcentre.ranksys.divmf.RankALSExampleParamTune /data/sidana/diversity/user_based_diversity/ml20m/not_diversity/unique_users /data/sidana/diversity/user_based_diversity/ml20m/not_diversity/unique_items /data/sidana/diversity/user_based_diversity/ml20m/not_diversity/train.csv /data/sidana/diversity/user_based_diversity/ml20m/not_diversity/test.csv /data/sidana/diversity/user_based_diversity/ml20m/not_diversity/features.ml20m

for type in divrankals_0.5_true_NONE_true divrankals_0.5_true_NONE_false divrankals_0.5_false_NONE_true divrankals_0.5_false_NONE_false divrankals_-0.5_true_NONE_true divrankals_-0.5_true_NONE_false divrankals_-0.5_false_NONE_true divrankals_-0.5_false_NONE_false

do
echo -e "$type" >> /data/sidana/diversity/user_based_diversity/ml20m/not_diversity/diversity_results
java -Xmx100G -cp RankSys-metrics-0.4.2-SNAPSHOT-jar-with-dependencies.jar:RankSys-core-0.4.2-SNAPSHOT-jar-with-dependencies.jar:RankSys-diversity-0.4.2-SNAPSHOT-jar-with-dependencies.jar:RankSys-novdiv-0.4.2-SNAPSHOT-jar-with-dependencies.jar:RankSys-novelty-0.4.2-SNAPSHOT-jar-with-dependencies.jar:RankSys-examples-0.4.2-SNAPSHOT-jar-with-dependencies.jar:RankSys-formats-0.4.2-SNAPSHOT-jar-with-dependencies.jar es.uam.eps.ir.ranksys.examples.MetricExample /data/sidana/diversity/user_based_diversity/ml20m/not_diversity/train.csv /data/sidana/diversity/user_based_diversity/ml20m/not_diversity/test.csv /data/sidana/diversity/user_based_diversity/ml20m/not_diversity/features.ml20m $type  1.0 >> /data/sidana/diversity/user_based_diversity/ml20m/not_diversity/diversity_results
done
