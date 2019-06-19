from random import random

# using linear function y = a * x - b where
# "x" is a value and "y" is the error to fix
# and it can be fixed using "a" and "b"
# this script finds appropriate "a" and "b" values
# for "x" and "y" list of numbers


x_list = [8,10,12,14]
y_list = [1.7,1.9,2.4,2.9]

# t_a = 0.1
t_b = 0.2

def linearb(x,y,a):
    return (a*x)-y

def lineara(x,y,b):
    return (y+b)/x

a_list = []
b_list = []

print(len(x_list))
for i in range(0, len(x_list)-1):
    print(i)
    a = lineara(x_list[i], y_list[i], t_b)
    b = linearb(x_list[i], y_list[i], a)
    for j in range(0,1000):
        y = (a*x_list[i])-b
        if y < y_list[i]:
            # a=a+0.1
            b=b-0.1
            a = lineara(x_list[i], y_list[i], b)
            b = linearb(x_list[i], y_list[i], a)
        elif y > y_list[i]:
            # a=a-0.1
            b=b+0.1
            a = lineara(x_list[i], y_list[i], b)
            b = linearb(x_list[i], y_list[i], a)
        else:
            print(y_list[i]," = ", a," * ",x_list[i], " - ", b)
            break
    a_list.append(a)
    b_list.append(b)


avr_a = 0
for i in a_list:
    avr_a = avr_a+i

avr_b = 0
for i in b_list:
    avr_b = avr_b+i

print(a_list, b_list)

avr_a = avr_a/len(a_list)
avr_b = avr_b/len(b_list)

print("a",avr_a, "b", avr_b)