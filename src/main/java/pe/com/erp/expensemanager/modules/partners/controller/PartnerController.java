package pe.com.erp.expensemanager.modules.partners.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pe.com.erp.expensemanager.modules.partners.model.Partner;
import pe.com.erp.expensemanager.modules.partners.services.interfaz.IPartnerService;
import pe.com.erp.expensemanager.properties.PropertiesExtern;

@RestController
@EnableTransactionManagement
@CrossOrigin(origins = {"http://localhost:4200", "*"})
@RequestMapping(path="/api/v1")
public class PartnerController {

    private static final Logger LOG = LoggerFactory.getLogger(pe.com.erp.expensemanager.modules.partners.controller.PartnerController.class);

    @Autowired
    PropertiesExtern properties;

    @Autowired
    IPartnerService iPartnerService;

    @GetMapping("owner/{idOwner}/partners")
    public List<Partner> findPartnersByOwnerId(@PathVariable Long idOwner) {
        return iPartnerService.findPartnersByOwnerId(idOwner);
    }

}
