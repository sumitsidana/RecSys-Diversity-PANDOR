mkdir -p /data/sidana/diversity/purch/data/validusers
mkdir -p /data/sidana/diversity/purch/data/nonvalidusers
for filename in divrankals_*;
do
	echo `wc -l "$filename"`
	grep -v "0.0$" "${filename}" > "validusers/${filename}"
	grep "0.0$" "${filename}" > "nonvalidusers/${filename}"
	echo `wc -l "validusers/${filename}" "nonvalidusers/${filename}"`
done
