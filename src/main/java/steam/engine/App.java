package steam.engine;

import steam.engine.display.display_main;

/**
 * Hello world!
 *
 */
public class App 
{
    display_main d = new display_main();
    public static void main( String[] args )
    {
        App a = new App();
        a.start();
    }
    public void start(){
        d.init();
    }
}
