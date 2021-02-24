VERSION="projectVersion \"$1\""

line=$(sed -n '/projectVersion/=' build.gradle)
echo $line
newline=$(expr $line[0] - 1)
sed  -i  "$line  d"   build.gradle
sed -i "$newline a\\$VERSION" build.gradle
echo $1
#git tag $0
#git push --tags