cd /data/sidana/diversity/user_based_diversity/ml20m/code/

for lambdaD in 0.5 -0.5
do
for item_importance in true false
  do
  for regularizer in NONE DQ LAPLACIAN_DQ P_LAPLACIAN_DQ
  do
  for use_similarity_array in true false
  do
  cat "diversity/${lambdaD}_${item_importance}_${regularizer}_${use_similarity_array}" "not_diversity/${lambdaD}_${item_importance}_NONE_${use_similarity_array}" > "all_users/${lambdaD}_${item_importance}_${regularizer}_${use_similarity_array}"
  done
  done
  done
done


