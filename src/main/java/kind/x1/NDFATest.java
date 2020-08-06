package kind.x1;

public class NDFATest extends Assertions 
{
    public void run()
    {
        testUniquePath();
        testAddSecondPath();
        testRemoveStateWhenNoTransitions();
        testLazyAccept();
        testCharSet();
        testTokenText();
    }
    
    public void testUniquePath ()
    {
        NDFA sut = new NDFABuilder().maxStates(1)
            .newState().start().on('a').on('b').accept(1)
            .build();
        //sut.showStateMap();
        sut.setInput ("ab");
        assertTrue ("testUniquePath: should have state 0 in start set", sut.hasState(0));
        assertTrue ("testUniquePath: should not have state 1 in start set", !sut.hasState(1));
        sut.step();
        assertTrue ("testUniquePath: should not have state 0 aftet step", !sut.hasState(0));
        assertTrue ("testUniquePath: should have state 1 after step", sut.hasState(1));
        assertTrue ("testUniquePath: should have produced token type 1", sut.nextToken().type() == 1);
        assertTrue ("testUniquePath: should have reset state after second step", sut.hasState(0) && !sut.hasState(1) && !sut.hasState(2));
    }

    public void testAddSecondPath ()
    {
        NDFA sut = new NDFABuilder().maxStates(2)
            .newState("0").start().newState("1").newState("2")  // create start state 0 & int states 1&2
            .newState("3").accept(1).newState("4").accept(2)    // accept states 3 (emits 10 and 4 (emits 2)
            .from("0").linkTo("1").ch('a').done()               // state 0 read 'a' -> 1
            .from("0").linkTo("2").ch('a').done()               // state 0 read 'a' -> 2 ALSO
            .from("1").linkTo("3").ch('b').done()               // state 1 read 'b' -> 3
            .from("2").linkTo("4").ch('c').done()               // state 2 read 'c' -> 4
            
            .build();
        //sut.showStateMap();
                    
        sut.setInput ("abac");
        sut.step();
        assertTrue ("testAddSecondPath: should not have state 0 aftet step", !sut.hasState(0));
        assertTrue ("testAddSecondPath: should have state 1 after step", sut.hasState(1));
        assertTrue ("testAddSecondPath: should have state 2 after step", sut.hasState(2));
        assertTrue ("testAddSecondPath: should produce token 1", sut.nextToken().type() == 1);
        assertTrue ("testAddSecondPath: should produce token 2", sut.nextToken().type() == 2);
    }
    public void testRemoveStateWhenNoTransitions ()
    {
        NDFA sut = new NDFABuilder().maxStates(2)
            .newState("0").start()
            .oneOrMore().ch('a').done()                 // creates state 1, which is a dead end
            .from("0").on('a').on('b').accept(1)        //states 2 & 3, which ar3n't
            .build();
        //sut.showStateMap();

        sut.setInput ("aabc");
        sut.step(); // states 1,2
        sut.step(); // state 1
        assertTrue ("testRemoveStateWhenNoTransitions: should have state 1 after step 2", sut.hasState(1));
        assertTrue ("testRemoveStateWhenNoTransitions: should not have state 2 after step 2", !sut.hasState(2));
        assertTrue ("testRemoveStateWhenNoTransitions: should have indicated error after step 3", sut.step() == NDFA.UPDATE_ERROR);
    }

    public void testLazyAccept ()
    {
        NDFA sut = new NDFABuilder().maxStates(2)
            .newState("0").start()
            .on('a').lazyAccept(1)
            .from("0").onStr("ab").accept(2)            
            .build();
        //sut.showStateMap();

        sut.setInput ("aab");
        assertTrue ("testLazyAccept: should produce token 1", sut.nextToken().type() == 1);
        assertTrue ("testLazyAccept: should produce token 2", sut.nextToken().type() == 2);
    }
    
    public void testCharSet ()
    {
        NDFA sut = new NDFABuilder().maxStates(1)
            .newState("0").start()
            .onSpecial(NDFA.LETTER)
            .onSpecial(NDFA.DIGIT).label("2")
            .onSpecial(NDFA.ANY).label("3")
            .linkTo("2").special(NDFA.DIGIT).done()
            .from("3").onSpecial(NDFA.ANY, NDFA.DIGIT)
            .onSpecial(NDFA.CUSTOM+0).accept(1)
            .build();
        //sut.showStateMap();

        sut.setCustomCharSet(0, "&^");
        sut.setInput ("a5/4,x&");
        sut.step();
        assertTrue ("testCharSet: step 1 state should be 1", sut.hasState(1));
        sut.step();
        assertTrue ("testCharSet: step 2 state should be 2", sut.hasState(2));
        sut.step();
        assertTrue ("testCharSet: step 3 state should be 3", sut.hasState(3));
        sut.step();
        assertTrue ("testCharSet: step 4 state should be 2", sut.hasState(2));
        assertTrue ("testCharSet: step 4 state should not be 4", !sut.hasState(4));
        sut.step();
        assertTrue ("testCharSet: step 5 state should be 3", sut.hasState(3));
        sut.step();
        assertTrue ("testCharSet: step 6 state should be 4", sut.hasState(4));
        assertTrue ("testCharset: should produce token 1", sut.nextToken().type() == 1);
    }
    
    public void testTokenText ()
    {
        NDFA sut = new NDFABuilder().maxStates(1)
            .newState().start().label("0").on('a').on('b').accept(1)
            .from("0").oneOrMore().ch('x').done().lazyAccept(2)
            .build();
        //sut.showStateMap();
        sut.setInput ("abxxabxxx");
        assertTrue ("testTokenText: should have produced token 'ab'", sut.nextToken().text().equals("ab"));
        assertTrue ("testTokenText: should have produced token 'xx'", sut.nextToken().text().equals("xx"));
        assertTrue ("testTokenText: should have produced token 'ab'", sut.nextToken().text().equals("ab"));
        assertTrue ("testTokenText: should have produced token 'xxx'", sut.nextToken().text().equals("xxx"));
    }
        
}
