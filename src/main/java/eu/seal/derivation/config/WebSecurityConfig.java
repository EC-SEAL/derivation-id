package eu.seal.derivation.config;


import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

 
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter implements InitializingBean, DisposableBean {
 
       
    /**
     * Defines the web based security configuration.
     * 
     * @param   http It allows configuring web based security for specific http requests.
     * @throws  Exception 
     */
    @Override  
    protected void configure(HttpSecurity http) throws Exception {    
        http        
            .authorizeRequests()
           		.antMatchers("*").permitAll()
           		//.antMatchers("/as/*").permitAll()
           		.antMatchers("/idboot/*").permitAll()
           		.antMatchers("/start/**").permitAll()
           		.antMatchers("/generate/*").permitAll()
           		.antMatchers("/saml/**").permitAll()
           		.antMatchers("/css/**").permitAll()
           		.antMatchers("/img/**").permitAll()
           		.antMatchers("/js/**").permitAll()
           		.anyRequest().authenticated();
        http
        		.logout()
        			.disable();	
    }

	@Override
	public void destroy() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub
		
	}
 


}
