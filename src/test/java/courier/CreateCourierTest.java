package courier;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.example.courier.Courier;
import org.example.courier.CourierAssertions;
import org.example.courier.CourierClient;
import org.example.courier.CourierGenerator;
import org.example.courier.Credentials;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;

public class CreateCourierTest {
    protected final CourierGenerator courierGenerator= new CourierGenerator();
    private CourierClient courierClient;
    private Courier courier;
    private CourierAssertions courierAssertions;
    int courierId;


    @Before
    @Step("Предусловия для создания курьера")
    public void setUp() {
        courierClient = new CourierClient();
        courier = courierGenerator.getRandomCourier();
        courierAssertions = new CourierAssertions();
    }

    @Test
    @DisplayName("Создание нового курьера")
    @Description("Курьера можно создать")
    public void courierCanBeCreated() {
        ValidatableResponse responseCreateCourier = courierClient.createCourier(courier);
        courierAssertions.successfullCreation(responseCreateCourier);
        Credentials credentials = Credentials.from(courier);
        ValidatableResponse responseLoginCourier = courierClient.loginCourier(credentials);
        courierId = responseLoginCourier.extract().path("id");
    }

    @Test
    @DisplayName("Создание курьера без заполнения поля логин")
    @Description("Курьера нельзя создать без логина. Проверяем статус код и сообщение об ошибке")
    public void courierCanNotBeCreatedWithoutLogin() {
        courier.setLogin(null);
        ValidatableResponse responseNullLogin = courierClient.createCourier(courier);
        courierAssertions.failedCreation(responseNullLogin);
    }

    @Test
    @DisplayName("Создание курьера без заполнения поля пароля")
    @Description("Курьера нельзя создать без пароля. Проверяем статус код и сообщение об ошибке")
    public void courierCanNotBeCreatedWithoutPassword() {
        courier.setPassword(null);
        ValidatableResponse responseNullPassword = courierClient.createCourier(courier);
        courierAssertions.failedCreation(responseNullPassword);
    }

    @Test
    @DisplayName("Создание курьера без заполнения поля пароля и логина")
    @Description("Курьера нельзя создать без пароля и логина. Проверяем статус код и сообщение об ошибке")
    public void courierCanNotBeCreatedWithoutLoginAndPassword() {
        courier.setLogin(null);
        courier.setPassword(null);
        ValidatableResponse responseNullFields = courierClient.createCourier(courier);
        courierAssertions.failedCreation(responseNullFields);
    }

    @Test
    @DisplayName("Создание курьера с существующими данными")
    @Description("Создание курьера с существующими данными. Проверяем статус код и сообщение.")
    public void courierCanNotBeCreatedWithExistingCreds() {
        courierClient.createCourier(courier);
        ValidatableResponse responseCreateCourier = courierClient.createCourier(courier);
        courierAssertions.existingCredsCreation(responseCreateCourier);
    }

    @After
    @Step("Удаление курьера")
    public void deleteCourier() {
        if (courierId != 0) {
            courierClient.deleteCourier(courierId);
        }
    }

}