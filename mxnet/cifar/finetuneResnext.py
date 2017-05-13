
python fine-tune.py --pretrained-model resnext-101 --num-layers 110 --load-epoch 0000 --gpus 0,1,2,3 --data-train cifar10_train.rec --data-val cifar10_val.rec --batch-size 128 --num-classes 10 --num-examples 50000 --image-shape 3,28,28 --num-epoch 300 --lr 0.05 --lr-factor 0.9 --lr-step-epochs 25,50,75,100,125,150,175,200,225,250,275,300  
