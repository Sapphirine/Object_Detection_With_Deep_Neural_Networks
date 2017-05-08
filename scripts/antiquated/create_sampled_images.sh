#!/bin/bash

#Apply random noise to create training images.
#for n in {1..4}; do
#    find $1 -name "* *.png" -type f -print0 | \
#    while read -d $'\0' f; do 
#	convert $f \( -size 256x256 xc:"gray(50%)" -seed 1000 +noise random -channel green -separate +channel \) \ -compose dissolve -define compose:args="15" $( printf %03d "$n" )_$f.png; 
#    done
#done

#for i in {1..2}; do dd if=/dev/urandom bs=1 count=1 of=file$i; done

#a=1

#convert 192.png \( -size 256x256 xc:"gray(50%)" -seed 1000 +noise random -channel green -separate +channel \) -compose dissolve -define compose:args="15" -composite zelda_graylinear_random_dissolve_15pctb.png

#find $1 -name "*.png" -type f -print0 | \

#for n in {0..100}; do
#	find $1 -name "*.png" -type f -print0 | \
#		while read -d $'\0' f; do convert "$f" \( -size 256x256 xc:"gray(50%)" +noise random -channel green -separate +channel \) -compose dissolve -define compose:args="15" -composite "$f"_$( printf %03d "$n" ); done
#done

#$( $RANDOM % $NUMBER_OF_POSSIBLE_ROTATIONS) -rotate 90 
NUMBER_OF_POSSIBLE_ROTATIONS=4
echo "Enter the filenamebase:"
read base
echo "Enter the source filename:"
read sourcefile
echo "Enter the number of copies desired samples:"
read numsamples
echo "Enter starting number."
read startnum
start=0
for (( c=$start; c<$numsamples; c++ )) do
	ROTATEAMT=$(($c % $NUMBER_OF_POSSIBLE_ROTATIONS))
	if [ "$startnum" -gt 0 ]; then
	indexnum=$(($c + $startnum))
	else
	indexnum=$c
	fi
	NEWFILENAME=$(printf "$base%05d.png" "$indexnum")
	#Resize image copy
	convert "$sourcefile" \( -resize 256x256! \) $NEWFILENAME

	#Add random noise to the image.
	convert "$NEWFILENAME" \( -size 256x256! xc:"gray(50%)" +noise random -channel green -separate +channel \) -compose dissolve -define compose:args="15" -composite $NEWFILENAME

	#Then, randomly rotate and add noise to image.
	if [ "$ROTATEAMT" -eq 1 ]; then
	convert "$NEWFILENAME" -rotate 90 "$NEWFILENAME"
	elif [ "$ROTATEAMT" -eq 2 ]; then
	convert "$NEWFILENAME" -rotate 180 "$NEWFILENAME"
	elif [ "$ROTATEAMT" -eq 3 ]; then 
	convert "$NEWFILENAME" -rotate 270 "$NEWFILENAME"
	fi
done

#find * -type f -print
