package grails.restapi.groovy

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class AnimalSpec extends Specification implements DomainUnitTest<Animal> {

     void "test domain constraints"() {
        when:
        Animal domain = new Animal()
        //TODO: Set domain props here

        then:
        domain.validate()
     }
}
