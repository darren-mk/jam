package grails.restapi.groovy

import grails.testing.web.controllers.ControllerUnitTest
import spock.lang.Specification

class GreetControllerSpec extends Specification implements ControllerUnitTest<GreetController> {

     void "test index action"() {
        when:
        controller.index()

        then:
        status == 200

     }
}
