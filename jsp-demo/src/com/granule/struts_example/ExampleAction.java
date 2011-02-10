package com.granule.struts_example;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * User: Dario Wünsch
 * Date: 27.09.2010
 * Time: 4:07:13
 */
public class ExampleAction extends Action {
    private static final String FIRST_FORWARD = "ex1";
    private static final String SECOND_FORWARD = "ex2";
    private static final String THIRD_FORWARD = "ex3";

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws Exception {
        String go = request.getParameter("go");
        if (go==null) go = FIRST_FORWARD;
        if (go.equals(FIRST_FORWARD))
          return mapping.findForward(FIRST_FORWARD);
        else if (go.equals(SECOND_FORWARD))
            return mapping.findForward(SECOND_FORWARD);
        else return mapping.findForward(THIRD_FORWARD);
    }
}
