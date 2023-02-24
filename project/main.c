#include <stdio.h>
#include <stdlib.h>
#include <conio.h>
#include <locale.h>

int main()
{
    setlocale(LC_CTYPE, "RUS");
    printf("Категорически приветствую!\n");
    int k = 12;
    int b = 5;
    printf("k = %i, b = %i", k, b);
    getch();
    return 0;
}
