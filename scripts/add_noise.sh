a=1
for i in *.png; do
  convert $i +noise Impulse $i
  let a=a+1
done
