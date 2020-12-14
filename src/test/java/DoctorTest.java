import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DoctorTest {
    private Doctor doctor;

    @Before
    public void initTest() {
        doctor = new Doctor("Alexandr", "Ivanov", "12.07.1985", "ENT doctor");
    }

    @After
    public void afterTest() {
        doctor = null;
    }

    @Test
    public void testTestToString() {
        Assert.assertNotNull(doctor.toString());
        Assert.assertEquals(doctor.toString(), "Ivanov Alexandr");
    }
}