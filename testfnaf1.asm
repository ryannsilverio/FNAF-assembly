.data
new_line: .asciiz "\n"
comma: .asciiz ", "
power: .asciiz "Power is at: "
time: .asciiz "It is "
time_end: .asciiz " o'lock\n"
.text

move $s1, 0
set $s0, 0
gen $t0, 100

roll $s0
loc $s1
ring new_line
cams $t0, $s0, $s1
flash $t0, $s0, $s1
close $t0, $s0, $s1
ring power
printp $t0
ring new_line

time $s2, 0
time $s3, 6

use $t0
ring power
printp $t0
ring new_line
boop

ring time
printp $s2
ring time_end
ring new_line

tick $s2
ring time
printp $s2
ring time_end







