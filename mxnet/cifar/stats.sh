
export NEW_LOG=
export FINETUNE_LOG=

grep Train $NEW_LOG | cut -d '=' -f 2 > new-train.txt
grep Validation $NEW_LOG | cut -d '=' -f 2 > new-val.txt

grep -v Batch $FINETUNE_LOG | grep Train |cut -d '=' -f 2 > finetune-train.txt
grep Validation $FINETUNE_LOG | cut -d '=' -f 2 > finetune-val.txt
