import java.io.*;
import java.util.*;
import java.lang.*;

import org.gnome.gtk.*;
import org.gnome.gdk.Event;

public class Main {
	public static void main(String[] args) {
		Gtk.init(args);

		/****************************************************************************************************************/


				Window window1 = new Window(); //Initial dialog
				Window window2 = new Window(); //if user wants to send files
				Window window3 = new Window(); //if user wants to recieve files


		/****************************************************************************************************************/

				FileChooserDialog fdialog = new FileChooserDialog("Select a file", window2, FileChooserAction.OPEN); //dialog for choosing a file to send

		/****************************************************************************************************************/


				window1.setTitle("Send or Recieve Files");
				window1.setDefaultSize(800, 600);
				window1.setPosition(WindowPosition.CENTER_ALWAYS);
				window1.hide();

				Button bsendfiles = new Button("Send Files");
				Button brecievefiles = new Button("Recieve Files");

				VBox verticalbox1 = new VBox(true, 5);

				verticalbox1.add(bsendfiles);
				verticalbox1.add(brecievefiles);

				window1.add(verticalbox1);


		/****************************************************************************************************************/


				window2.setTitle("Send Files");
				window2.setDefaultSize(800, 600);
				window2.setPosition(WindowPosition.CENTER_ALWAYS);
				window2.hide();

				FindHosts hostfinder = new FindHosts();
				hostfinder.start();

				Button bopenfiledialog = new Button("Choose a file");
				Button bsend = new Button("Send File");
				Button bip = new Button("Set Host IP");
				Button bport = new Button("Set Host Port");
				Button bback1 = new Button("Back");
				Button brefresh1 = new Button("Refresh");
				Button bshowavailhosts = new Button("Show Hosts");

				Entry eip = new Entry("127.0.0.1");
				Entry eport = new Entry("12450");
				Entry etimeout = new Entry("200");

				Label lip1 = new Label("Host IP");
				Label lport = new Label("Host Port");
				Label lfile = new Label("No file selected");
				Label ltimeout = new Label("Timeout");

				Grid grid1 = new Grid();

				grid1.attach(bopenfiledialog, 1, 2, 1, 1); //widget, column, row, width, height
				grid1.attach(bsend, 1, 4, 1, 1);
				grid1.attach(lip1, 3, 1, 1, 1);
				grid1.attach(eip, 3, 2, 1, 1);
				grid1.attach(lport, 3, 3, 1, 1);
				grid1.attach(eport, 3, 4, 1, 1);
				grid1.attach(bip, 4, 2, 1, 1);
				grid1.attach(bport, 4, 4, 1, 1);
				grid1.attach(lfile, 1, 5, 1, 1);
				grid1.attach(bback1, 1, 6, 1, 1);
				grid1.attach(bshowavailhosts, 1, 7, 1, 1);

				window2.add(grid1);


		/****************************************************************************************************************/


				window3.setTitle("Recieve Files");
				window3.setDefaultSize(800, 600);
				window3.setPosition(WindowPosition.CENTER_ALWAYS);
				window3.hide();

				Recieve recieve = new Recieve();

				Button bback2 = new Button("Back");

				Label lip2 = new Label("IP: ");
				try {
					 lip2 = new Label("IP: " + Recieve.getIP());
				} catch (UnresolvableErrorException e) {
					MessageDialog md = new MessageDialog(window3, true, MessageType.ERROR, ButtonsType.YES_NO, "Couldn\'t get IP of this machine, continue?");
					md.setTitle("Error");
					ResponseType resp = md.run();

					if (resp == ResponseType.NO) {
						Gtk.mainQuit();
						System.exit(0);
					}
					else
						lip2 = new Label("IP: Error");

					md.hide();
				}

				Label lport2 = new Label("Port: ");

				Entry eport2 = new Entry("12450");

				Grid grid2 = new Grid();

				grid2.attach(lip2, 1, 1, 1, 1);
				grid2.attach(lport2, 1, 2, 1, 1);
				grid2.attach(eport2, 2, 2, 1, 1);
				grid2.attach(bback2, 1, 3, 1, 1);

				window3.add(grid2);


		/****************************************************************************************************************/

		//all signals go below regardless of what window the widget belongs to

		/****************************************************************************************************************/


				window1.connect(new Window.DeleteEvent() {
					public boolean onDeleteEvent(Widget source, Event event) {
						Gtk.mainQuit();
						System.exit(0);
						return false;
					}
				});

				window2.connect(new Window.DeleteEvent() {
					public boolean onDeleteEvent(Widget source, Event event) {
						Gtk.mainQuit();
						hostfinder.interrupt();
						System.exit(0);
						return false;
					}
				});

				window3.connect(new Window.DeleteEvent() {
					public boolean onDeleteEvent(Widget source, Event event) {
						Gtk.mainQuit();
						recieve.interrupt();
						System.exit(0);
						return false;
					}
				});

				bback1.connect(new Button.Clicked() {
					public void onClicked(Button source) {
						window2.hide();
						hostfinder.interrupt();
						window1.showAll();
					}
				});

				bback2.connect(new Button.Clicked() {
					public void onClicked(Button source) {
						window3.hide();
						recieve.interrupt();
						window1.showAll();
					}
				});

				bopenfiledialog.connect(new Button.Clicked() {
					public void onClicked(Button source) {
						ResponseType response = fdialog.run();
						fdialog.hide();
						String file = "";

						if (response == ResponseType.OK)
							file = fdialog.getFilename();
						else if(response == ResponseType.CANCEL)
							file = "null";

						if (!file.equals("null")) {
							try {
								Send.setFileThroughGUI(file);
								lfile.setLabel("File Selected: " + fdialog.getFilename());
							} catch (FileNotFoundException fne) {
								MessageDialog md = new MessageDialog(window2, true, MessageType.ERROR, ButtonsType.OK, "File not found");
								md.setTitle("Error");
								md.run();
								md.hide();
								file = "";
							} catch (FileIsDirectoryException fide) {
								MessageDialog md = new MessageDialog(window2, true, MessageType.ERROR, ButtonsType.OK, "Selected file is a directory, this is not supported");
								md.setTitle("Error");
								md.run();
								md.hide();
								file = "";
							}
						}
						else
							return;
					}
				});

				bsend.connect(new Button.Clicked() {
					public void onClicked(Button source) {
						if (!Send.isFileSelected()) {
							MessageDialog md = new MessageDialog(window2, true, MessageType.ERROR, ButtonsType.OK, "No file selected");
							md.setTitle("Error");
							md.run();
							md.hide();
						}
						else if (Send.g_host.equals("127.0.0.1")) {
							MessageDialog md = new MessageDialog(window2, true, MessageType.ERROR, ButtonsType.OK, "No host chosen");
							md.setTitle("Error");
							md.run();
							md.hide();
						}
						try {
							Send send = new Send();
							send.start();
						} catch (LocalhostException e) {
							return;
						}
					}
				});

				bsendfiles.connect(new Button.Clicked() {
					public void onClicked(Button source) {
						window1.hide();
						window2.showAll();
					}
				});

				brecievefiles.connect(new Button.Clicked() {
					public void onClicked(Button source) {
						window1.hide();
						recieve.start();
						window3.showAll();
					}
				});

				bip.connect(new Button.Clicked() {
					public void onClicked(Button source) {
						Send.g_host = eip.getText();
						MessageDialog md1 = new MessageDialog(window2, true, MessageType.INFO, ButtonsType.OK, "IP was changed");
						md1.setDefaultResponse(ResponseType.CLOSE);
						md1.setTitle("IP changed");
						md1.run();
						md1.hide();
					}
				});

				bport.connect(new Button.Clicked() {
					public void onClicked(Button source) {
						try {
							Send.setPortThroughGUI(Integer.parseInt(eport.getText()));
						} catch (InvalidPortException | NumberFormatException e) {
							MessageDialog dialog = new MessageDialog(window2, true, MessageType.ERROR, ButtonsType.OK, "User set a port that isn\'t allowed");
							dialog.setTitle("Invalid Port");
							dialog.setDefaultResponse(ResponseType.CLOSE);
							ResponseType resp = dialog.run();
							dialog.hide();
							eport.setText("12450");
						}
						MessageDialog md1 = new MessageDialog(window2, true, MessageType.INFO, ButtonsType.OK, "Port was changed");
						md1.setDefaultResponse(ResponseType.CLOSE);
						md1.setTitle("Port changed");
						md1.run();
						md1.hide();
					}
				});

				eip.connect(new Entry.Activate() {
					public void onActivate(Entry source) {
						Send.g_host = eip.getText();
						MessageDialog md1 = new MessageDialog(window2, true, MessageType.INFO, ButtonsType.OK, "IP was changed");
						md1.setDefaultResponse(ResponseType.CLOSE);
						md1.setTitle("IP changed");
						md1.run();
						md1.hide();
					}
				});

				eport.connect(new Entry.Activate() {
					public void onActivate(Entry source) {
						try {
							Send.setPortThroughGUI(Integer.parseInt(eport.getText()));
						} catch (InvalidPortException | NumberFormatException e) {
							MessageDialog dialog = new MessageDialog(window2, true, MessageType.ERROR, ButtonsType.OK, "User set a port that isn\'t allowed");
							dialog.setTitle("Invalid Port");
							dialog.setDefaultResponse(ResponseType.CLOSE);
							ResponseType resp = dialog.run();
							dialog.hide();
							eport.setText("12450");
						}
						MessageDialog md1 = new MessageDialog(window2, true, MessageType.INFO, ButtonsType.OK, "Port was changed");
						md1.setDefaultResponse(ResponseType.CLOSE);
						md1.setTitle("Port changed");
						md1.run();
						md1.hide();
					}
				});

				bshowavailhosts.connect(new Button.Clicked() {
					public void onClicked(Button source) {
						if (!hostfinder.isAlive()) {
							Window window = new Window();
							window.setTitle("Available Hosts");
							window.setPosition(WindowPosition.MOUSE);
							window.hide();

							DataColumnString hostcolumn;
							DataColumnInteger portcolumn;
							ListStore model1 = new ListStore(new DataColumn[] {
								hostcolumn = new DataColumnString(),
								portcolumn = new DataColumnInteger()
							});

							TreeIter treerow1;
							TreeViewColumn treecol1;

							for (Map.Entry<String,Integer> entry : FindHosts.g_openhosts.entrySet()) {
								treerow1 = model1.appendRow();
								model1.setValue(treerow1, hostcolumn, entry.getKey());
								model1.setValue(treerow1, portcolumn, entry.getValue());
							}

							TreeView tree1 = new TreeView(model1);

							CellRendererText text1;

							treecol1 = tree1.appendColumn();
							treecol1.setTitle("Host");
							text1 = new CellRendererText(treecol1);

							treecol1 = tree1.appendColumn();
							treecol1.setTitle("Port");
							text1 = new CellRendererText(treecol1);

							window.add(tree1);
							window.showAll();
						}
						else {
							MessageDialog dialog = new MessageDialog(window2, true, MessageType.ERROR, ButtonsType.OK, "Still discovering hosts\nThis may take a while");
							dialog.setTitle("Still discovering hosts");
							dialog.run();
							dialog.hide();
						}
					}
				});

				eport2.connect(new Entry.Activate() {
					public void onActivate(Entry source) {
						try {
							Recieve.setPortThroughGUI(Integer.parseInt(eport.getText()));
						} catch (InvalidPortException | NumberFormatException e) {
							MessageDialog dialog = new MessageDialog(window2, true, MessageType.ERROR, ButtonsType.OK, "User set a port that isn\'t allowed");
							dialog.setTitle("Invalid Port");
							dialog.setDefaultResponse(ResponseType.CLOSE);
							ResponseType resp = dialog.run();
							dialog.hide();
							eport.setText("12450");
						}
						MessageDialog md1 = new MessageDialog(window3, true, MessageType.INFO, ButtonsType.OK, "Port was changed");
						md1.setDefaultResponse(ResponseType.CLOSE);
						md1.setTitle("Port changed");
						md1.run();
						md1.hide();
					}
				});


		/****************************************************************************************************************/


				window1.showAll();
				Gtk.main();
	}
}
