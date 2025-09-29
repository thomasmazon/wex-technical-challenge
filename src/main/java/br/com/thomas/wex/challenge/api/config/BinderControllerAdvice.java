package br.com.thomas.wex.challenge.api.config;

import org.springframework.core.annotation.Order;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

/**
 * @author Thomas J. Mazon de Oiveira
 */
@ControllerAdvice
@Order(10000)
public class BinderControllerAdvice {

    @InitBinder

    public void setAllowedFields(WebDataBinder dataBinder) {

         String[] denylist = new String[]{"class.*", "Class.*", "*.class.*", "*.Class.*"};

         dataBinder.setDisallowedFields(denylist);

    }

}
