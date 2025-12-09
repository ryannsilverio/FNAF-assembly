.data
welcome: .asciiz "Hello? Hello hello? Simulate a typical night shift as a security guard at Freddy Fazbear's Pizza!\n\nNight 1: You clocked in at "
new_line: .asciiz "\n"
choices: .asciiz "Here are a list of things you can do as night guard, please type the number of the choice you wish to execute:\n"
op1: .asciiz "1. Check Cameras\n"
op2: .asciiz "2. Use your flashlight\n"
op3: .asciiz "3. Close the doors\n"
op4: .asciiz "4. Do nothing\n"
invalid: .asciiz "You have chosen an invalid option, please try again.\n"
tell_time: .asciiz "It is currently "
start_time: .asciiz "12"
tell_time_end: .asciiz " o'clock.\n"
win_text: .asciiz "Yay! You have made it to the end of your shift! We hope to see you tomorrow night."
no_power_text: .asciiz "Uh oh... You lost power...\n"
see_power: .asciiz "Your power percentage is at: "
power_end: .asciiz "%\n"
.text

time $s2, 0	# time reg 
time $s3, 9	# end at 9 am
ring welcome
equiv $s2, 0, twelve1
printp $s2
time1_end:
ring tell_time_end
ring new_line
gen $t0, 50 	# energy reg
gen $t1, 0 	# user input reg	
move $s1, 0	# location reg
runto game_loop

twelve1:
ring start_time
runto time1_end

twelve2:
ring start_time
runto time2_end

game_loop:
set $s0, 0	# animatronic reg
display $s0	# display office
ring tell_time	# print time
equiv $s2, 0, twelve2
printp $s2
time2_end:
ring tell_time_end
ring new_line

# end conditions:
# if you reached the end time, you survive and win
equiv $s2, $s3, win	# at exactly six, you win
# check if you ran out of power, gameover
equiv $t0, 0, no_power
check $t0, $zero, no_power

ring see_power
printp $t0
ring power_end
ring new_line

roll $s0	# randomize active animatronic
loc $s1		# randomize location

input_loop:
ring choices	# ask player for choice input
ring op1
ring op2
ring op3
ring op4
ans $t1		# put answer in input reg, branch to labels
ring new_line
options:
equiv $t1, 1, cameras
equiv $t1, 2, flashlight
equiv $t1, 3, close_door
equiv $t1, 4, input_end	
ring invalid
runto input_loop
input_end:

# check end game conditions again
runto check_jumpscare
check_jumpscare_end:

# check if you ran out of power, gameover
equiv $t0, 0, no_power
check $t0, $zero, no_power

# if none met, increment and use some energy
use $t0
check $s2, $s3, increment
runto end 	# ensure no operations are performed after

flashlight:
flash $t0, $s0, $s1
equiv $s1, 4, display_anim
equiv $s1, 5, display_anim
display $zero
ring see_power
printp $t0
ring power_end
ring new_line
runto input_loop

cameras:
cams $t0, $s0, $s1
# display new power value to keep track
ring see_power
printp $t0
ring power_end
ring new_line
runto input_loop

close_door:
close $t0, $s0, $s1
display $s0
ring see_power
printp $t0
ring power_end
ring new_line
runto input_end		# animatronic and location reset, so no more input after this

check_jumpscare:
# if the animatronics are in any of the hallways and you have not closed the door by the time we check, you get jumpscared
equiv $s1, 4, jumpscare_end
equiv $s1, 5, jumpscare_end
runto check_jumpscare_end

display_anim:
display $s0
runto input_loop

increment:
tick $s2
runto game_loop

no_power:
ring no_power_text
# freddy jumpscares
set $s0, 3

jumpscare_end:
jumpscare

win:
ring win_text
end:



