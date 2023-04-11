module namespace h2i = "hex-to-integer";

declare namespace math = 'http://www.w3.org/2005/xpath-functions/math';

declare function h2i:hex-to-integer($hex as xs:string) as xs:integer
{
  xs:integer
  (
    sum
    (
      for $digit at $i in string-to-codepoints($hex)
      return math:pow(16, string-length($hex) - $i)
        * (
            if ($digit = 48 to 57) then
              $digit - 48
            else if ($digit = 65 to 70) then
              $digit - 55
            else if ($digit = 97 to 102) then
              $digit - 87
            else
              error(xs:QName('fn:error'), 'Invalid hexadecimal digit: ' || $digit)
          )
    )
  )
};