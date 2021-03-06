package com.nimbits.server.api;

import com.nimbits.client.enums.client.CommunicationType;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.mqtt.Mqtt;
import com.nimbits.client.model.mqtt.MqttModel;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.service.client.ClientService;
import com.nimbits.server.service.email.EmailService;
import com.nimbits.server.service.entity.EntityService;
import com.nimbits.server.service.mqtt.MqttService;
import com.nimbits.server.service.search.SearchService;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

/**
 * User: benjamin
 * Date: 5/29/12
 * Time: 12:29 PM
 * Copyright 2012 Nimbits Inc - All Rights Reserved
 */

@Controller
public class ServiceController {
    private static final Logger log = Logger.getLogger(ServiceController.class.getName());

    @Resource(name="entityService")
    private EntityService entityService;

    @Resource(name="searchService")
    private SearchService searchService;

    @Resource(name="emailService")
    private EmailService emailService;

    @Resource(name="clientService")
    private ClientService clientService;

    @Resource(name="mqttService")
    private MqttService mqttService;





    @RequestMapping(value="service/search", method= RequestMethod.GET)
    public String search(
            @RequestParam("search") String dangerousSearchText,
            @RequestParam("format") String format
            , ModelMap model) {

        model.addAttribute("TEXT",searchService.processSearch(dangerousSearchText, format));

        return "searchResults";
    }

    @RequestMapping(value="service/entity", method= RequestMethod.POST)
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public void processRequest(
            @RequestParam("entity") String json,
            @RequestParam("action") String actionParam,
            @RequestParam("instance") String instanceURL
    ){

        try {
            entityService.processEntity(json, actionParam,  instanceURL);
        } catch (NimbitsException e) {
            log.severe(e.getMessage());
            log.severe(ExceptionUtils.getStackTrace(e));
        }

    }

    @RequestMapping(value="service/mqtt", method= RequestMethod.POST)
    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public void receiveMqttMessage(
            @RequestParam("data") String data

    ) throws NimbitsException {
            log.severe(data);
        Mqtt mqtt = GsonFactory.getInstance().fromJson(data, MqttModel.class);
             mqttService.receiveMessage(mqtt);

    }


    @RequestMapping(value="service/dev", method=RequestMethod.GET)

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public void processDevRequest( @RequestParam("email") String email,
                                   @RequestParam("name") String name,
                                   @RequestParam("company") String company,
                                   @RequestParam("request") String request,
                                   ModelMap model)
    {

        try {
            EmailAddress em = CommonFactory.createEmailAddress(email);
            clientService.addClientCommunication(em, "", company, name, request, CommunicationType.devRequest);

            model.addAttribute("TEXT","OK");
        } catch (NimbitsException e) {
            model.addAttribute("TEXT",e.getMessage());
        }



    }

    @RequestMapping(value="service/location", method= RequestMethod.POST)
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public void receiveLocation(
            @RequestParam("entity") String json,
            @RequestParam("Location") String location
    ){
        log.info("call to service Location" + json + " " + location);
        try {
            entityService.processLocation(json, location);
        } catch (NimbitsException e) {
            log.severe(e.getMessage());
            log.severe(ExceptionUtils.getStackTrace(e));
        }

    }

    @RequestMapping(value="service/location.html", method= RequestMethod.GET)
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public void getEntities(ModelMap model){

        List<String[]> list = entityService.getLocations();
        List<String> map = new ArrayList<String>(list.size());
        StringBuilder sb = new StringBuilder();

        sb.append(" var neighborhoods = [\n");

        for (String[] s : list) {
            String loc = s[4];


            String fixed = loc.replace("POINT", "").replace(" ", ", ");
            while (true) {
                if (map.contains(fixed)) {
                    fixed = moveLocation(fixed);

                }
                else {
                    map.add(fixed);
                    break;
                }
            }
            sb.append("new google.maps.LatLng").append(fixed).append(",\n");
        }

        sb.append("        ];");
        sb.append("\n");


        sb.append(" var uuids = [\n");

        for (String[] s : list) {

            sb.append("'").append(s[5]).append("?uuid=").append(s[0]).append("'").append(",\n");
        }

        sb.append("];");
        sb.append("\n");

        sb.append(" var desc = [\n");

        for (String[] s : list) {
            String fixed = fixString(s[1] + " " + s[2]);
            sb.append("'").append(fixed).append("'").append(",\n");
        }
        sb.append("];");
        sb.append("\n");
        model.addAttribute("TEXT",sb.toString());
    }

    public String fixString(String s) {
        return s.replace("'", "\\\'")
                .replace("\"", "\\\"");

    }

    public String moveLocation(final String sample) {
        String[] split = sample.replace("(", "").replace(")", "").split(",");
        double lat = Double.valueOf(split[0]);
        double lng = Double.valueOf(split[1]);
        Random random = new Random();
        lat += random.nextDouble() / 1000 * (random.nextInt(2) == 1 ? 1 : -1);
        lng += random.nextDouble() / 1000 * (random.nextInt(2) == 1 ? 1 : -1);
        DecimalFormat format = new DecimalFormat("#.#####");
        return  "(" + Double.valueOf(format.format(lat)) + ", " + Double.valueOf(format.format(lng)) + ")";
    }


}
