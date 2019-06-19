annotation=$1
dataset=$2
python3 flow --model cfg/giancfgs/tiny-yolo-gian-voc.cfg --load weights/tiny-yolo-voc.weights --train --annotation $annotation --dataset $dataset --epoch 500
