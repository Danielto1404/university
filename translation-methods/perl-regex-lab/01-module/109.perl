while (<>) {
	print if !/(^ | $)/;
}