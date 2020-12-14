import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class PatientTest {
    public Patient patient;

    @Before
    public void setUp() throws Exception {
        patient = new Patient("Anton", "Lapenko", "01.08.1989");
    }

    @After
    public void tearDown() throws Exception {
        patient = null;
    }

    @Test
    public void testToString() {
        Assert.assertNotNull(patient.toString());
        Assert.assertEquals(patient.toString(), "Lapenko Anton");
    }
}