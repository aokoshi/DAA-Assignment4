package daa;

import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class AlgorithmsTest {
    final Algorithms.Distance[] distances={Algorithms::recursive, Algorithms::table, Algorithms::twoRows};
    int d(Algorithms.Distance f,String a,String b) { return f.distance(a,b,new Algorithms.Counter()); }
    String random(Random r,int n) { StringBuilder b=new StringBuilder(); for(int i=0;i<n;i++) b.append("ABC".charAt(r.nextInt(3))); return b.toString(); }
    @Test void referencesAndEmpty() {
        for(var f:distances) { assertEquals(3,d(f,"kitten","sitting")); assertEquals(2,d(f,"flaw","lawn")); assertEquals(5,d(f,"intention","execution")); assertEquals(3,d(f,"","abc")); assertEquals(3,d(f,"abc","")); assertEquals(0,d(f,"abc","abc")); assertEquals(0,d(f,"","")); }
    }
    @Test void randomDistanceAgreement() { Random r=new Random(42); for(int i=0;i<100;i++) { String a=random(r,r.nextInt(7)),b=random(r,r.nextInt(7)); int expected=d(distances[0],a,b); for(var f:distances) assertEquals(expected,d(f,a,b)); } }
    @Test void prefixes() { assertArrayEquals(new int[]{0,1,2,0,1,2,3,3},Algorithms.lps("AAACAAAA")); assertArrayEquals(new int[]{0,0,1,2,0,1,2,3,4},Algorithms.lps("ABABCABAB")); }
    void check(String t,String p,List<Integer> result) { assertEquals(result,Algorithms.naive(t,p,new Algorithms.Counter())); assertEquals(result,Algorithms.kmp(t,p,new Algorithms.Counter())); }
    @Test void searchReferences() { check("ABABDABACDABABCABAB","ABABCABAB",List.of(10)); check("AAAAA","AA",List.of(0,1,2,3)); check("ABABABA","ABA",List.of(0,2,4)); check("abc","abcd",List.of()); check("","a",List.of()); }
    @Test void emptyPatternThrows() { assertThrows(IllegalArgumentException.class,()->Algorithms.naive("abc","",new Algorithms.Counter())); assertThrows(IllegalArgumentException.class,()->Algorithms.kmp("abc","",new Algorithms.Counter())); }
    @Test void randomSearchAgreement() { Random r=new Random(99); for(int i=0;i<100;i++) { String t=random(r,r.nextInt(50)),p=random(r,1+r.nextInt(8)); assertEquals(Algorithms.naive(t,p,new Algorithms.Counter()),Algorithms.kmp(t,p,new Algorithms.Counter())); } }
}
