package ru.rostvertolplc.osapr.tc06.components;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Shell;

public class ShowInfoDialog extends TitleAreaDialog {

	private String selectedButton;
	private String title, bodyMsg, multiMsg;
	private int msgType; // IMessageProvider
	private Text textMultiMsg;

	/**
	 * @param parentShell
	 *            - Parent Shell
	 * @param title
	 *            - Title of the dialogue.
	 * @param bodyMsg
	 *            - Body message of the dialogue.
	 * 
	 * @param msgType
	 *            - 'IMessageProvider.INFORMATION ' Can be one of: NONE ERROR
	 *            INFORMATION WARNING
	 */
	public ShowInfoDialog(Shell parentShell, String title, String bodyMsg,
			String multiMsg, int msgType) { // for type see:
		// IMessageProvider

		super(parentShell);

		// Set labels.
		this.title = title;
		this.bodyMsg = bodyMsg;

		// set type
		this.msgType = msgType;
		this.multiMsg = multiMsg;

		// avoid help button poping up.
		this.setHelpAvailable(false);
		selectedButton = null;
	}

	/** Dialogue constructor */
	@Override
	public void create() {

		super.create();

		// The 'Message' of a TitleArea dialogue only spans 1-2 lines. Then text
		// is cut off.
		// It is not very efficient for longer messages.
		// Thus we utilize it as a 'title' and instaed we appeng a label to act
		// as body. (see below).
		setMessage(this.title, this.msgType); //$NON-NLS-1$
		// setTitle(); //not used.

		// Set the size of the dialogue.
		// We avoid hard-coding size, instead we tell it to figure out the most
		// optimal size.
		// this.getShell().setSize(650, 550); //Hard-Coded = bad.
		this.getShell().setSize(getInitialSize());
	}

	/**
	 * Return the buttonID of the button that the user selected if he pressed
	 * ok.
	 * 
	 * @return ButtonID of selected button.
	 */
	public String getSelectedButton() {
		return selectedButton;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layout = new GridLayout(1, false);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		container.setLayout(layout);

		Label label = new Label(container, 0);
		label.setText(this.bodyMsg);
		textMultiMsg = new Text(container, 527114);
		textMultiMsg.setBackground(parent.getDisplay().getSystemColor(25));
		GridData gridData = new GridData(272);
		gridData.grabExcessVerticalSpace = true;
		gridData.grabExcessHorizontalSpace = true;
		gridData.heightHint = convertVerticalDLUsToPixels(200);
		gridData.widthHint = convertHorizontalDLUsToPixels(400);
		textMultiMsg.setLayoutData(gridData);
		textMultiMsg.setText(this.multiMsg);
		return area;
	}

	// save content of the Text fields because they get disposed
	// as soon as the Dialog closes
	protected void saveInput() {

	}

	/** Called when the ok button is pressed */
	@Override
	protected void okPressed() {
		saveInput(); // save input.
		super.okPressed(); // close dialogue
	}

	protected boolean isResizable() {
		return true;
	}

}