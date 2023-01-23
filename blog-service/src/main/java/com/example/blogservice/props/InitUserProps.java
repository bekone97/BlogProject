package com.example.blogservice.props;

import com.example.blogservice.model.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@ConfigurationProperties(prefix = "init")
@Getter
@Setter
public class InitUserProps {
    private List<User> users;
}
