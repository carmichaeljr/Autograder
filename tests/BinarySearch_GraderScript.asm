.data
	values:			.space 800	#800 bytes, 200 words
	arrayNumberPrompt:	.string	"Enter a number to add to the array: "
	indexPrompt:		.string "Index: "
	valuePrompt:		.string	"Value: "
	searchPrompt:		.string "What value are you looking for? "
	
.text
	.globl main
	main:
		jal ra, getValues			#Load values in array
		mv s0, a0				#Save num elements
		
		la a0, searchPrompt			#String to print
		li a7, 4				#Print string
		ecall
		li a7, 5				#Read int
		ecall
		mv s1, a0				#Save item to search for
		
		
		la a2, values				#Set array address
		li a3, 0				#Set start index
		mv a4, s0				#Get num elements in array
		addi a4, a4, -1				#Sub 1 to get last index
		mv a5, s1				#Set value to search for
		jal ra, binarySearch			#Proc call, this is where your code takes over
		mv s0, a0				#Save search result
		la a0, indexPrompt			#Set prompt to print
		li a7, 4				#Print string
		ecall
		mv a0, s0				#Load int to print
		li a7, 1				#Print int
		ecall
		li a0, 10				#Set to print newline
		li a7, 11				#Print char
		ecall
		
		la a0, valuePrompt			#Set prompt to print
		li a7, 4				#Set to print string
		ecall
		la t0, values
		slli s0, s0, 2				#Get address from index
		add s0, t0, s0				#Add array address offset
		lw s0, 0(s0)				#Get value from array
		mv a0, s0				#Set int to print
		li a7, 1				#Set to print int
		ecall
		li a0, 10				#Set newline char
		li a7, 11				#Set to print char
		ecall
		
		li a7, 10				#Set syscall to exit program
		ecall					#Exit
	
	#a0: num of elements inputed
	getValues:
		add t0, zero, zero			#Init loop cntr
		li t3, 200				#sets array size
		la t1, values				#Load array address
		getValuesLoop:
			la t4, arrayNumberPrompt
			add a0, zero, t4		#Copies buffer address to a0
 			li a7, 4			#Sets syscall to print null-terminated string
 			ecall
			li a7, 5			#Set syscall argument to 5 (read int from console)
			ecall
			mv t2, a0			#Place syscall rv into the given register
			beqz t2, getValuesLoopEnd	#If input is 0, end
			sw t2, 0(t1)			#Save input
			addi t1, t1, 4			#Increment array address
			addi t0, t0, 1			#Increment loop cntr
			beq t0, t3, getValuesLoopEnd	#If loop cntr=max array size, exit
			j getValuesLoop
		getValuesLoopEnd:
			mv a0, t0			#Set rv
			jalr zero, ra, 0
