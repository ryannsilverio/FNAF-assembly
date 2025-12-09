# Program 2: FNAF Roulette
# pulls a random number from 0-6, if it lands on a certain number you get jumpscared
.data
win: .asciiz "No one was jumpscared!"
ctr: .asciiz "The loop ran "
ctr_end: .asciiz " times.\n"
.text
move $s1, 0
gen $t0, 0
gen $t1, 20
gen $t2, 0 	# loop counter

Loop:
loc $s1
equiv $s1, 5, scare
check $t0, $t1, increment
# print how many times the loop ran
ring ctr
printp $t2
ring ctr_end
runto end

increment:
addp $t0, 5
tick $t2
runto Loop

scare:
ring ctr
printp $t2
ring ctr_end
jumpscare

end:
ring win
