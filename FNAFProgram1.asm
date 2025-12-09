# Program 1 (looping through locations, with the animatronic and its location being printed to I/O, and the view being displayed)
.data
start: .asciiz "Your starting power is: "
message: .asciiz "Your ending power is: "
ask: .asciiz "Continue?\n0. No\n1.Yes\n"
new_line: .asciiz "\n"
.text

set $s0, 0	# initializing all registers
move $s1, 0
time $s2, 0
time $s3, 6
gen $t0, 30
gen $t1, 0 # input

ring start
printp $t0
ring new_line

game_loop:
roll $s0	# rolling random animatronic
loc $s1 	# rolling random location
cams $t0, $s0, $s1 # prints animatronic and location, also displays
ring ask
ans $t1
ring new_line

# branches based on input
equiv $t1, 0, end_game
tick $s2 	# updates time by an hour, starts at 0 -> 1, 1 -> 2, and so on
check $s2, $s3, game_loop 	# checks if less than, if greater than or equal to, skips

ring message
printp $t0	# printing energy value in $t0

end_game:
