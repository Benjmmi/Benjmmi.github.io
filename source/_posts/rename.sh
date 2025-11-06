for i in $(find . -type f );
do 
a=`grep -oE "[0-9]{4}-[0-9]{1,2}-[0-9]{1,2}" -m 1 $i`
if [[ ! -z $a ]];
then
	name=${i##*/}
	result=$(echo $i | grep "/")

	if [[ "$result" != "" ]]
	then
		path=${i%/*}
	fi

	name=`echo $path/$a-$name`
	mv $i $name
fi
done
