directory="/data/sidana/diversity/purch/data"
mkdir -p /data/sidana/diversity/purch/data/validusers
mkdir -p /data/sidana/diversity/purch/data/nonvalidusers
for filename in "$directory/divrankals_*";
do
	grep -v "0.0$" "${filename}" > "validusers/${filename}"
	grep "0.0$" "${filename}" > "nonvalidusers/${filename}"
done
