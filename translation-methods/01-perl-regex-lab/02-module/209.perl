while (<>) {
    s/\((.*?)\)/()/g;
    print;
}
