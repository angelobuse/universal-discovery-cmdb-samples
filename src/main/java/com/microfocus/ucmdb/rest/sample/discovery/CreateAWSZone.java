/**
 * © Copyright 2011 - 2020 Micro Focus or one of its affiliates
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.microfocus.ucmdb.rest.sample.discovery;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.microfocus.ucmdb.rest.sample.utils.PayloadUtils;
import com.microfocus.ucmdb.rest.sample.utils.RestApiConnectionUtils;

import java.io.Console;
import java.io.IOException;
import java.util.Scanner;

public class CreateAWSZone {
    private String rootURL;

    public CreateAWSZone(String rootURL) {
        this.rootURL = rootURL;
    }
    public static void main(String[] args) throws Exception {
        String hostname;
        String port;
        String username;
        String password;

        if (args.length < 4) {
            Scanner sc = new Scanner(System.in);
            System.out.print("Please enter hostname/IP of UCMDB Server: ");
            hostname = sc.hasNextLine() ? sc.nextLine() : "";
            System.out.print("Please enter port of UCMDB Server: ");
            port = sc.hasNext() ? sc.next() : "";
            System.out.print("Please enter username for UCMDB: ");
            username = sc.hasNext() ? sc.next() : "";
            Console console = System.console();
            password = new String(console.readPassword("Please enter password for UCMDB: "));
        } else {
            hostname = args[0];
            port = args[1];
            username = args[2];
            password = args[3];
        }

        String rootURL = RestApiConnectionUtils.buildRootUrl(hostname, port,false);

        // authenticate
        String token = RestApiConnectionUtils.loginServer(rootURL, username, password);


        // start the task
        CreateAWSZone task = new CreateAWSZone(rootURL);
        task.execute(token);
    }

    private void execute(String token) throws Exception {

        // check if new UI backend enabled
        RestApiConnectionUtils.ensureZoneBasedDiscoveryIsEnabled(rootURL, token);

        int count = 1;
        String content = PayloadUtils.loadContent(this.getClass().getSimpleName(), count);
        count ++;
        // create AWS credential
        String credentialId = RestApiConnectionUtils.doPost(rootURL + "dataflowmanagement/credentials", token, content, "CREATE AWS CREDENTIAL.");
        credentialId = credentialId.substring(1,credentialId.length() - 1);

        // create AWS credential group
        content = PayloadUtils.loadContent(this.getClass().getSimpleName(), count);
        count ++;
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode o = objectMapper.readValue(content, JsonNode.class);
        ((ObjectNode)o.get("credentials").get(0).get("protocols")).putArray("awsprotocol").add(credentialId);
        RestApiConnectionUtils.doPost(rootURL + "discovery/credentialprofiles", token, o.toString(), "CREATE AWS CREDENTAIL GROUP.");


        // create AWS job group.
        content = PayloadUtils.loadContent(this.getClass().getSimpleName(), count);
        count ++;
        RestApiConnectionUtils.doPost(rootURL + "discovery/discoveryprofiles", token, content, "CREATE AWS JOB GROUP.");

        // create the zone
        content = PayloadUtils.loadContent(this.getClass().getSimpleName(), count);
        count ++;
        RestApiConnectionUtils.doPost(rootURL + "discovery/managementzones", token, content, "CREATE AWS ZONE.");

    }
}
