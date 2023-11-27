package ru.job4j.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import org.springframework.test.web.servlet.MvcResult;
import ru.job4j.Job4jUrlShortcutApplication;
import ru.job4j.domain.Shortcut;
import ru.job4j.domain.Site;
import ru.job4j.repository.ShortcutRepository;
import ru.job4j.repository.SiteRepository;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = Job4jUrlShortcutApplication.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ShortcutControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private String login;
    private String password;
    private final String siteName = "site.com";
    private final String url = "google.com";
    private String token;
    private String shortcutCode;

    @Autowired
    private SiteRepository siteRepository;
    @Autowired
    private ShortcutRepository shortcutRepository;

    @AfterAll
    public void resetDb() {
        shortcutRepository.deleteAll();
        siteRepository.deleteAll();
    }

    @Test
    @Order(1)
    public void whenCreateNewSiteAccountThenGetCreatedStatusAndSite() throws Exception {
        Site site = new Site();
        site.setSite(siteName);
        MvcResult result = mockMvc.perform(post("/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(site)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("registration").value(true))
                .andExpect(jsonPath("login").isString())
                .andExpect(jsonPath("password").isString())
                .andReturn();
        Site resultSite = new ObjectMapper().readValue(result.getResponse().getContentAsString(),
                Site.class);
        login = resultSite.getLogin();
        password = resultSite.getPassword();
    }

    @Test
    @Order(2)
    public void whenCreateNewSiteAccountWithoutSiteAddressThenGetConflictStatus() throws Exception {
        Site site = new Site();
        MvcResult result = mockMvc.perform(post("/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(site)))
                        .andDo(print())
                        .andExpect(status().isBadRequest())
                        .andReturn();
    }

    @Test
    @Order(3)
    public void whenLoginWithRegisteredLoginAndPasswordSuccessfully() throws Exception {
        Site site = new Site();
        site.setLogin(login);
        site.setPassword(password);
        MvcResult result = mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(site)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        token = result.getResponse().getHeader("Authorization");
    }

    @Test
    @Order(4)
    public void whenLoginWithRegisteredLoginAndPasswordNotSuccessfully() throws Exception {
        Site site = new Site();
        site.setLogin("some");
        site.setPassword("some");
        MvcResult result = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(site)))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @Test
    @Order(5)
    public void whenCreateShortcutForUrlThenGetOkStatusAndShortcutCode() throws Exception {
        Shortcut shortcut = new Shortcut();
        shortcut.setUrl(url);
        MvcResult result = mockMvc.perform(post("/convert")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(shortcut))
                .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("shortcut").isString())
                .andReturn();
        Shortcut resultshortcut = new ObjectMapper().readValue(result.getResponse().getContentAsString(),
                Shortcut.class);
        shortcutCode = resultshortcut.getShortcut();
    }

    @Test
    @Order(6)
    public void whenRedirectToUrlUsingExistingShortcutThenRedirectedAndGetFoundStatus() throws Exception {
        MvcResult result = mockMvc.perform(get("/redirect/" + shortcutCode)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isFound())
                .andExpect(header().stringValues("Location", url))
                .andReturn();
    }

    @Test
    @Order(7)
    public void whenRedirectToUrlUsingNotExistingShortcutThenGetNotFoundStatus() throws Exception {
        MvcResult result = mockMvc.perform(get("/redirect/2jrcstw")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    @Order(8)
    public void whenAskForStatisticsThenGetIt() throws Exception {
        MvcResult result = mockMvc.perform(get("/statistic")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].url").value(url))
                .andExpect(jsonPath("$[0].total").value(1))
                .andReturn();
    }

}