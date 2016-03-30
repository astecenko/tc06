package ru.rostvertolplc.osapr.tc06.handlers;

import java.util.regex.Pattern;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentForm;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.util.MessageBox;
import org.eclipse.jface.dialogs.*;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import ru.rostvertolplc.osapr.tc06.components.MsgsList;
import ru.rostvertolplc.osapr.forms.ShowInfoDialog;

public class MainHandler extends AbstractHandler {

	private StringBuilder sInfoMsg;
	
	/**
	 * The constructor.
	 */
	public MainHandler() {
	}

	private boolean CheckComponent(TCComponent tcComp1, String prop1,
			String msg1, StringBuilder msgList1, boolean prizn1) {
		try {
			if (tcComp1.getProperty(prop1).equals("")) {
				if (prizn1)
					msgList1.append(", ");
				msgList1.append(msg1);
				return true;
			} else
				return prizn1;
		} catch (TCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return prizn1;
	}

	private boolean CheckForm(TCComponentForm tcComp1, String prop1,
			String msg1, StringBuilder msgList1, boolean prizn1) {
		try {
			TCProperty prop0 = tcComp1.getFormTCProperty(prop1);

			if ((prop0 != null) && (prop0.getStringValue().equals(""))) {
				if (prizn1)
					msgList1.append(", ");
				msgList1.append(msg1);
				return true;
			} else
				return prizn1;
		} catch (TCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return prizn1;
	}

	boolean CheckNode(TCComponentBOMLine bomLine1, String pathString1) {

		try {
			AIFComponentContext[] arrayOfAIFComponentContext = null;
			try {
				arrayOfAIFComponentContext = bomLine1.getChildren();
			} catch (TCException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if (arrayOfAIFComponentContext != null) {
				int i = arrayOfAIFComponentContext.length;
				TCComponentBOMLine bomLine2;
				for (int j = 0; j < i; j++) {
					boolean b = false;
					StringBuilder s1 = new StringBuilder("");

					bomLine2 = (TCComponentBOMLine) arrayOfAIFComponentContext[j]
							.getComponent();

					b = CheckComponent(bomLine2,
							MsgsList.sPropRevReleaseStatusList,
							MsgsList.sNoRevReleaseStatusList, s1, b);
					b = CheckComponent(bomLine2, MsgsList.sPropSequenceNo,
							MsgsList.sNoSequenceNo, s1, b);
					b = CheckComponent(bomLine2, MsgsList.sPropQuantity,
							MsgsList.sNoQuantity, s1, b);
					b = CheckComponent(bomLine2, MsgsList.sPropZona,
							MsgsList.sNoZona, s1, b);

					TCComponentItemRevision itemRevision = null;
					itemRevision = bomLine2.getItemRevision();
					if (itemRevision != null) {
						TCComponentForm rev_form = null;
						rev_form = (TCComponentForm) itemRevision
								.getRelatedComponent("IMAN_master_form_rev");
						if (rev_form != null) {
							/*
							 * b = CheckForm(rev_form, MsgsList.sPropMassaHR03,
							 * MsgsList.sNoMassaHR03, s1, b);
							 */
							b = CheckForm(rev_form, MsgsList.sPropFormatHR68,
									MsgsList.sNoFormatHR68, s1, b);
							b = CheckForm(rev_form, MsgsList.sPropGabaritHR07,
									MsgsList.sNoGabaritHR07, s1, b);
							b = CheckForm(rev_form, MsgsList.sPropDiametrHR100,
									MsgsList.sNoDiametrHR100, s1, b);

							try {
								TCProperty prop0 = rev_form
										.getFormTCProperty(MsgsList.sPropMassaHR03);

								if ((prop0 != null)
										&& (prop0.getDoubleValue() == 0.0)) {
									if (b)
										s1.append(", ");
									s1.append(MsgsList.sNoMassaHR03);
								}
							} catch (TCException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							if (bomLine2.getItem().getType()
									.equals("H47_Detal")
									|| bomLine2.getItem().getType().equals(
											"H47_SE"))
								b = CheckForm(rev_form,
										MsgsList.sPropIzveschHR75,
										MsgsList.sNoIzveschHR75, s1, b);
						}

						if (bomLine2.getItem().getType().equals("H47_Detal")
								|| bomLine2.getItem().getType().equals(
										"H47_Standart_Izd")
								|| bomLine2.getItem().getType().equals(
										"H47_GeomMaterial")) {
							if (itemRevision
									.getRelatedComponent(MsgsList.sPropMaterial) == null) {
								if (b)
									s1.append(", ");
								s1.append(MsgsList.sNoMaterial);
								b = true;
							}

							if (bomLine2.getItem().getType().equals(
									"H47_Standart_Izd")) {
								if (!Pattern.matches(MsgsList.sTestStdItemId,
										bomLine2.getItem().getProperty(
												"item_id"))) {
									if (b)
										s1.append(", ");
									s1.append(MsgsList.sNoStandartIzd);
									b = true;
								}
							}

						}
					}

					if (b) {
						sInfoMsg.append(pathString1).append(
								MsgsList.sNodeDelimiter).append(
								bomLine2.getProperty(MsgsList.sFormatedTitle))
								.append("\n\t").append(s1).append("\n");
					}

					if (bomLine2.hasChildren()) {
						if (!CheckNode(bomLine2, pathString1
								+ MsgsList.sNodeDelimiter
								+ bomLine2.getProperty(MsgsList.sFormatedTitle)))
							return false;
					}
				}
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public Object execute(ExecutionEvent event) throws ExecutionException {
		TCComponentBOMLine localTCComponentBOMLine;
		IWorkbenchWindow window;
		window = HandlerUtil
				.getActiveWorkbenchWindowChecked(event);

		sInfoMsg = new StringBuilder("");
		AbstractAIFUIApplication currentApplication = AIFUtility
				.getCurrentApplication();
		
		AIFComponentContext context1 = currentApplication.getTargetContext();
		if (context1 == null) {
			MessageBox.post("Сборка не выбрана!", "Teamcenter Error",
					MessageBox.ERROR);
			return null;
		}

		try {
			localTCComponentBOMLine = (TCComponentBOMLine) context1
					.getComponent();
		} catch (ClassCastException localClassCastException) {
			MessageBox.post(
					"Выбранный объект не является элементом структуры изделия",
					"Teamcenter Error", MessageBox.ERROR);
			return null;
		}
		
		
			try {
				CheckNode(localTCComponentBOMLine, localTCComponentBOMLine
						.getProperty(MsgsList.sFormatedTitle));
			} catch (TCException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (sInfoMsg.length() > 0) {
				ShowInfoDialog myDialog = new ShowInfoDialog(window.getShell(),
						MsgsList.sDialogTitle,
						"Обнаружены незаполненные атрибуты элементов", sInfoMsg
								.toString(), IMessageProvider.WARNING);
				myDialog.open();
			} else
				MessageBox.post(
						"Незаполненные атрибуты элементов структуры не обнаружены",
						MsgsList.sDialogTitle, MessageBox.INFORMATION);
		

		

		

		return null;
	}
}