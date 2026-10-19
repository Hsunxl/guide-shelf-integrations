package io.github.hsunxl.guideshelf;

import java.util.*;

public final class IntegrationTest {
    record Entry(String id,String title) {}
    public static void main(String[] args) {
        var nativeBook=new Entry("ae2:guide","native");
        var result=DiscoveryPolicy.merge(List.of(nativeBook),List.of(new Entry("ae2:guide","duplicate"),new Entry("ponder:library","Ponder")),Entry::id);
        assert result.size()==2;
        assert result.get(0)==nativeBook;
        assert DiscoveryPolicy.merge(result,result,Entry::id).size()==2;
        var ignored=Set.of("ponder:library");
        assert result.stream().filter(e->!ignored.contains(e.id())).count()==1;
        assert DiscoveryPolicy.wikiPath("oritech").equals("wiki/oritech");
        assert DiscoveryPolicy.wikiPath("oracle-index").equals("wiki/oracle-index");
        for(String invalid:Arrays.asList(null,"","Upper","with space","name:space","中文")) assert DiscoveryPolicy.wikiPath(invalid)==null;
        assert DiscoveryPolicy.merge(List.<Entry>of(),List.<Entry>of(),Entry::id).isEmpty();
        System.out.println("PASS discovery: stable IDs, duplicate suppression, native precedence, ignore filtering, invalid wiki IDs, absent providers");
    }
}
