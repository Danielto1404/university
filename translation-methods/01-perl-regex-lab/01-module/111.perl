
#
#   0						1
#  ---					   ---
#  | |	   1           0   | |
# (q0 ~ 0) -> (q1 ~ 1) -> (q2 ~ 2)
#   ^          | ^         |
#   |    1     | |    0    |
#   ------------ -----------
#

# From automata to regex

# q0 = 0 * q0 + 1 * q1
# q1 = 1 * q0 + 0 * q2
# q2 = 0 * q1 + 1 * q2 ~ 0 * q1 + 1^

# q1 = 1 * q0 + (00 * q1 + 0(1^)) ~ 1 * q0 + (00^ + 0(1^))
# q0 = (  0^ | (  11^ | 1(  00^ | 0(1^)  )  )  ) ~
# q0 = (  0+ | (  1  (01*0)*  1  )+  )+


while (<>) {
	print if /^(0+|(1(01*0)*1)+)+$/;
}