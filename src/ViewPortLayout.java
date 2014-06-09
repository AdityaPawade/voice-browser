import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.miginfocom.swing.MigLayout;
import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.plaf.basic.BasicTextPaneUI;
import javax.swing.text.EditorKit;
import javax.swing.text.JTextComponent;

import com.inet.html.InetHtmlConfiguration;
import com.inet.html.InetHtmlEditorKit;


public class ViewPortLayout extends JPanel {

	/**
	 * Create the panel.
	 * 
	 */
	public JEditorPane editorPane;
	public URL url;
	public ViewPortLayout() {
		setLayout(new MigLayout("", "[grow]", "[grow]"));
		// We use a kit instance override here to avoid that the UI sets the kit by
        // the content type
        final InetHtmlEditorKit kit = new InetHtmlEditorKit();
        kit.setDefaultConfig( InetHtmlConfiguration.getBrowserConfig() );
		
		editorPane = new JEditorPane();
		editorPane.setOpaque( false );
		editorPane.setEditorKit( kit );
		editorPane.setUI( new BasicTextPaneUI(){
            @Override
            public EditorKit getEditorKit( JTextComponent tc ) {
                return kit;
            }
        });
		JScrollPane webScroll = new JScrollPane( editorPane );
        webScroll.setDoubleBuffered( true );
        Listener l = new Listener();
        editorPane.addHyperlinkListener( l );
        editorPane.setEditable( false );
        
		//editorPane.setAutoscrolls(true);
		//editorPane.get
		add(webScroll, "cell 0 0,grow");
		try {
			url = new URL("https://www.google.co.in/");
			editorPane.setPage(url);			
		}catch(MalformedURLException ex)
		{
			ex.printStackTrace();
			editorPane.setText("<h1>Sorry The Page Can Not Be Displayed</h1>");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			editorPane.setText("<h1>Sorry The Page Can Not Be Displayed</h1>");
		}
		

	}
	
	/**
     * A simple hyperlink listener which reacts only on click
     */
    private class Listener implements HyperlinkListener {

        /**
         * {@inheritDoc}
         */
        public void hyperlinkUpdate( HyperlinkEvent e ) {
            if( e.getEventType() == HyperlinkEvent.EventType.ACTIVATED ){
            	try {
        			url = e.getURL();
        			editorPane.setPage(url);			
        		} catch (IOException ex) {
        			// TODO Auto-generated catch block
        			ex.printStackTrace();
        			editorPane.setText("<h1>Sorry The Page Can Not Be Displayed</h1>");
        		}
            }
        }
        
    }

}
