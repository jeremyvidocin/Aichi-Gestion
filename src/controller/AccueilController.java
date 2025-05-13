package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import model.User;
import views.AccueilView;

public class AccueilController {
	private AccueilView accueilView; 
	private User currentUser; 
	
	public AccueilController(User currentUser) {
		this.currentUser = currentUser;
		accueilView = new AccueilView(currentUser);
	
	
	
	accueilView.getDecoButton().addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            deconnexion();
        }
	});
	}
	
	private void deconnexion() {
		accueilView.dispose();
		
	}
}
