.data
newline: .asciiz "\n"
str0: .asciiz entra al if


.text
.globl main

func2:
    # prologo funcion
    sub $sp, $sp, 32
    sw $ra, 0($sp)

    li $t0, 5
    li $t1, 1
    mult $t0, $t1
    mflo $t2
    move $v0, $t2

    # epilogo funcion
    lw $ra, 0($sp)
    add $sp, $sp, 32
    jr $ra

mi:
    # prologo funcion
    sub $sp, $sp, 32
    sw $ra, 0($sp)

L0:
    li $t3, 0
    li $t4, 10
    slt $t5, $t4, $t3
    beqz $t5, L3
L1:
    li $t6, 0
    beqz $t6, L7
L6:
    la $a0, str0
    li $v0, 4
    syscall
    la $a0, newline
    li $v0, 4
    syscall
    j L5
L7:
L5:
L2:
    li $t7, 0
    addi $t0, $t7, 1
    j L0
L3:
    li $t1, 1
    move $v0, $t1

    # epilogo funcion
    lw $ra, 0($sp)
    add $sp, $sp, 32
    jr $ra

main:
    # prologo main
    sub $sp, $sp, 32
    sw $ra, 0($sp)

    lw $t2, -8($sp)
    move $a0, $t2
    li $v0, 1
    syscall
    la $a0, newline
    li $v0, 4
    syscall
    li $t3, 1
    move $a0, $t3
    li $v0, 1
    syscall
    la $a0, newline
    li $v0, 4
    syscall
    li.s $f12, 6.7
    neg $t5, $f12
    move $a0, $t5
    li $v0, 1
    syscall
    la $a0, newline
    li $v0, 4
    syscall
    li $t6, 1
    li $t7, 0
    sne $t0, $t6, $t7
    sw $t0, -20($sp)
    li $t1, 1
    move $v0, $t1

    # epilogo main
    lw $ra, 0($sp)
    add $sp, $sp, 32
    li $v0, 10
    syscall


