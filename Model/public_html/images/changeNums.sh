for i in ${1}; do

   echo img_${i}_full.jpg = img_${2}_full.jpg
   mv img_${i}_full.jpg img_${2}_full.jpg

   echo img_${i}_thumb.jpg = img_${2}_thumb.jpg
   mv img_${i}_thumb.jpg img_${2}_thumb.jpg

done
