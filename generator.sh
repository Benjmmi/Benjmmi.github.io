title=$1
tags=$2

name=${title##*/}

result=$(echo $title | grep "/")
if [[ "$result" != "" ]]
then
	path=${title%/*}
fi

mkdir -p _posts/$path
#Remove punctuations
#parse=`echo $title | sed -e "s/[[:punct:]]//g"`
#Replace space with -
parse=`echo $name | sed -e "s/ /-/g"`

create_date=`date +"%Y-%m-%d"`
create_date_time=`date +"%Y-%m-%d %H:%M:%S"`
md_name="_posts/$path/$create_date-$parse.md"

if [ ! -d "${md_name}" ];then
echo "gen file $md_name"
echo "---" > $md_name
echo "layout : post" >> $md_name
echo "title: '$name'" >> $md_name
	echo "date: '$create_date_time'" >> $md_name
	echo "tags: $tags" >> $md_name
	echo "subclass: 'post tag-$tags'" >> $md_name
	echo "categories: ''" >> $md_name
	echo "cover: ''" >> $md_name
	echo "---" >> $md_name
fi
cat $md_name

