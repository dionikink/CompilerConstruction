int max = 0;
int i = 0;
while (i < a.length) {
    if (a[i] > max) {
        max = a[i];
    }
    i = i + 1;
}
printf("Max: %d%n", max);

// CC.6.3: The multiplication is needed for the new address of the next integer.