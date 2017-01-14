#!/usr/bin/perl

my @res_types = ("string", "layout", "id");

my $f = $ARGV[0];

if (length($f) == 0) {
  $f = 'build/generated/source/r/debug/net/neevek/android/lib/paginizecontrib/R.java'
  #print("usage: ./generateP <filename>\n");
  #exit;
}
open my $fh, $f or die "failed to open file: " . $f;

my $out_file = "src/main/java/net/neevek/android/lib/paginizecontrib/P.java";
open my $out_handle, '>', $out_file or die "failed to open output file: " . $out_file;

print $out_handle "package net.neevek.android.lib.paginizecontrib;\n\n\n";

print $out_handle "/* this file is auto-generated, DON'T edit it.*/\n";
print $out_handle "public final class P { \n";

my $should_print = 0;
while(<$fh>) {
  if (/static final class ([^ ]+) / && (grep /^$1$/, @res_types)) {
    $should_print = 1;
    print $out_handle $_;
  } elsif (/\s*}\s*;?\s*/) {
    if ($should_print) {
      print $out_handle $_;
    }
    $should_print = 0;
  }

  if ($should_print && /paginize_contrib_/) {
    s/^(.*?) (?:final )?int (paginize_contrib_[^=]+)=.*/$1 final String $2="$2";/;
    print $out_handle $_;
  }
}

print $out_handle "}\n";
