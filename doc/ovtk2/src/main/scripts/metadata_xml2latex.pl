#!/usr/bin/env perl

my $inF = 0;
my $inD = 0;
my $temp = "";
my %data;
my $id = "";
my $out = "";

while(my $line = <>){
	chomp($line);
	
	if ($line =~ /^.*\<id\>(.*?)\<\/id\>.*$/){
		$data{$id} = $out;
		$id = $1;
		$out = "";
		$out = $out."\\item{$1}\\\\\n";
	}
	elsif ($line =~ /^.*\<fullname\>(.*?)\<\/fullname\>.*$/){
		$out = $out."Full name: $1\\\\\n";
	}
	elsif ($line =~/^.*\<fullname\>(.*?)$/){
		$out = $out."Full name: $1 ";
		$inF = 1; # we're in the Fullname section
	}
	elsif ($inF == 1 and $line =~ /^(.*?)\<\/fullname\>.*$/){
		$out = $out."$1\\\\\n";
		$inF = 0;
	}
	elsif ($inF == 1){
		$out = $out."$line ";
	}
	elsif ($line =~ /^.*\<description\>(.*?)\<\/description\>.*$/){
		$out = $out."Description: $1\n\n";
	}
	elsif ($line =~/^.*\<description\>(.*?)$/){
		$out = $out."Description: $1 ";
		$inD = 1; # we're in the Description section
	}
	elsif ($inD == 1 and $line =~ /^(.*?)\<\/description\>.*$/){
		$out = $out."$1\n\n";
		$inD = 0;
		$temp = "";
	}
	elsif ($inD == 1){
		$out = $out."$line ";
	}
}

foreach $key (sort (keys (%data))) {
	$data{$key} =~ s/\s+/ /gi;
	$data{$key} =~ s/\_/\\_/gi;
	$data{$key} =~ s/\>/\$\>\$/gi;
	if ($data{$key} =~ m/(http[s]?)/) {	
		$url = $1;
		$url =~ s/\s+$//g;
		$data{$key} =~ s/$url/\n\\url\{$url/g;
		$data{$key} =~ s/\ $//g;
		$data{$key} = $data{$key}."\}";
	}
	print $data{$key}."\n\n";
}