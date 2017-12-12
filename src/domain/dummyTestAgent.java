package myExamples;

import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.Behaviour;


public class dummyTestAgent extends Agent {

	protected void setup() {
			
		DummyBehaviour dummy = new DummyBehaviour();
		addBehaviour(dummy);
	}
	
	class DummyBehaviour extends OneShotBehaviour {

		public void action() {
			System.out.println("Agent "+getName());
			try{
				Document doc = Jsoup.connect("http://www.aemet.es/").get();
				System.out.println(doc.title());
				
				/*
				Elements newsHeadlines = doc.select("#mp-itn b a");
				for (Element headline : newsHeadlines){
					System.out.println(headline.attr("title") + headline.absUrl("href"));
				}
				*/
			}catch(Exception e){
				System.out.println("Exception "+e);
			}
		}
	}
}
