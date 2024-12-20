package com.microservice.auth.service.implementation;

import com.microservice.auth.client.ManageUserClient;
import com.microservice.auth.persistence.model.enums.State;
import com.microservice.auth.presentation.advice.ResourceNotFoundException;
import com.microservice.auth.presentation.dto.*;
import com.microservice.auth.presentation.dto.HTTP.MessageDTO;
import com.microservice.auth.service.interfaces.AuthService;
import com.microservice.auth.utils.ActivationCodeGenerator;
import com.microservice.auth.utils.JwtUtils;
import com.microservice.auth.utils.ValidatePassword;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthServiceImpl implements AuthService {
    final JwtUtils jwtUtilsService;
    final ManageUserClient manageUserClient;
    final PasswordEncoder passwordEncoder;
    final ValidatePassword validatePassword;
    final ActivationCodeGenerator activationCodeGenerator;
    final MailServiceImpl mailService;
    String logoUrl = "https://res.cloudinary.com/dqgykik8d/image/upload/v1726960044/Mi_boleta_agivea.png";


    public AuthServiceImpl(JwtUtils jwtUtilsService, ManageUserClient manageUserClient, PasswordEncoder passwordEncoder, ValidatePassword validatePassword, ActivationCodeGenerator activationCodeGenerator, MailServiceImpl mailService) {
        this.jwtUtilsService = jwtUtilsService;
        this.manageUserClient = manageUserClient;
        this.passwordEncoder = passwordEncoder;
        this.validatePassword = validatePassword;
        this.activationCodeGenerator = activationCodeGenerator;
        this.mailService = mailService;
    }


    @Override
    public TokenDTO loginClient(LoginClientDTO loginClientDTO) throws Exception {

        try {
            //verificar si tiene datos
            if (loginClientDTO == null) {
                throw new IllegalArgumentException("Por favor ingrese un valor valido");
            }

            //Buscar el usuario en el msvc UserManage
            ResponseEntity<MessageDTO<ClientDTO>> userEntity = manageUserClient.getClient(loginClientDTO.emailAddress(), loginClientDTO.password()) ;
            MessageDTO<ClientDTO> user = userEntity.getBody();

            //Verificar que exista ese usuario
            if (user == null || user.getData() == null) {
                throw new ResourceNotFoundException("No se ha encontrado un usuario asocioado a la información proporcionada");
            }

            //Verificar que no el usuario esté activo
            if (user.getData().state() != State.ACTIVE) return new TokenDTO(State.INACTIVE.toString());

            // Crear los atributos del token de autenticación
            Map<String, Object> authToken = new HashMap<>();
            authToken.put("role", user.getData().role());
            authToken.put("name", user.getData().name());
            authToken.put("idUser", user.getData().idUser());

            //generar el token
            String token = jwtUtilsService.generarToken(user.getData().emailAddress(), authToken);

            if (token == null) {
                throw new IllegalArgumentException("No se ha generado el token correctamente");
            }

            // Crear un objeto TokenDto para devolver el token generado
            return new TokenDTO(token);

        }catch (NullPointerException | IllegalArgumentException e){
            return new TokenDTO(null);
        }
    }

    @Override
    public StateDTO registerClient(RegisterClientDTO registerUserDto) {

        try {
            //Verificar que no esté vacio el formulario de registro
            if (registerUserDto == null) {
                throw new IllegalArgumentException("Por favor ingrese un valor valido");
            }
            //verificar que sea válida la contraseña con los parametros de seguiridad
            if (!validatePassword.validarContrasena(registerUserDto.password())) {
                throw new IllegalArgumentException("La contraseña no cumple con los parámetros especificados");
            }
            //obtener el estado del registro del mscv manageUser
            ResponseEntity<MessageDTO<StateDTO>> stateResponseEntity = manageUserClient.registerClient(registerUserDto);
            MessageDTO<StateDTO> stateRegister = stateResponseEntity.getBody();
            if (stateRegister == null || stateRegister.getData() == null) {throw new NullPointerException("state register not found");}


            if (stateRegister.getData().stateRegister() != State.SUCCESS) {
                throw new IllegalArgumentException("El estado del registro no es satisfactorio");
            }

            //generar código de verificación
            String codeActivation = activationCodeGenerator.generateActivationCode();

            //Enviar el codigo de verificación para ser guardado
             manageUserClient.saveCodeValidation(codeActivation, registerUserDto.idUser());

            String emailContent =
                    "<!DOCTYPE html>\n" +
                            "<html lang=\"es\">\n" +
                            "<head>\n" +
                            "    <meta charset=\"UTF-8\">\n" +
                            "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                            "    <title>Bienvenido a Nuestra Aplicación</title>\n" +
                            "</head>\n" +
                            "<body style=\"font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 20px;\">\n" +
                            "    <table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"background-color: #f8f8f8; border-radius: 5px;\">\n" +
                            "        <tr>\n" +
                            "            <td style=\"padding: 20px; text-align: center;\">\n" +
                            "                <h1 style=\"color: #EDB017;\">¡Bienvenido Mi Boleta, " + registerUserDto.name() + "!</h1>\n" +
                            "<img src=\"" + logoUrl + "\" alt=\"Logo de Mi Boleta\" style=\"max-width: 100%; height: 100px; margin-bottom: 20px;\" />\n" +
                            "                <p style=\"font-size: 16px;\">Gracias por registrarte. Estamos emocionados de tenerte con nosotros.</p>\n" +
                            "                <div style=\"background-color: #ffffff; border-radius: 5px; padding: 20px; margin: 20px 0;\">\n" +
                            "                    <p style=\"font-size: 18px; margin-bottom: 10px;\">Tu código de validación es:</p>\n" +
                            "                    <h2 style=\"color: #400101; font-size: 32px; letter-spacing: 5px; margin: 0;\">" + codeActivation + "</h2>\n" + //
                            "                </div>\n" +
                            "                <p style=\"font-size: 16px;\">Por favor, utiliza este código para validar tu cuenta en nuestra aplicación.</p>\n" +
                            "                <p style=\"font-size: 14px; color: #666;\">Si no has solicitado esta cuenta, puedes ignorar este correo.</p>\n" +
                            "                <div style=\"margin-top: 30px; padding-top: 20px; border-top: 1px solid #ddd;\">\n" +
                            "                    <p style=\"font-size: 14px; color: #888;\">\n" +
                            "                        Si tienes alguna pregunta, no dudes en contactarnos en \n" +
                            "                        <a href=\"mailto:unilocalsoporte@gmail.com\" style=\"color: #4CAF50;\">unilocalsoporte@gmail.com</a>\n" +
                            "                    </p>\n" +
                            "                </div>\n" +
                            "            </td>\n" +
                            "        </tr>\n" +
                            "    </table>\n" +
                            "</body>\n" +
                            "</html>";


            //Enviar email con código
            mailService.sendMail(registerUserDto.emailAddress(),"Código de autentificación", emailContent);
            StateDTO stateDTO = stateRegister.getData();

           return new StateDTO(stateDTO.stateRegister(),stateDTO.stateUser(),stateDTO.idUser());



        }catch (IllegalArgumentException | NullPointerException e) {
           return new StateDTO(State.ERROR,null,null);

        }
    }

    @Override
    public TokenDTO loginMod(LoginClientDTO loginClientDTO)  {
        return null;
    }

    @Override
    public State activationAccount(String code, String emailAddress)  {

        try {
            System.out.println("email es="+emailAddress);
            System.out.println(code);
            System.out.println("si llega por lo menos aquí");
            if(!StringUtils.hasText(code) && !StringUtils.hasText(emailAddress)) throw new IllegalArgumentException("error  ingrese un código de verifiación");
            ResponseEntity<MessageDTO<ClientDTO>> user = manageUserClient.getUserByEmail(emailAddress);

            MessageDTO<ClientDTO> messageDTO = user.getBody();
            System.out.println(messageDTO.getData());
            if (messageDTO == null) {
                throw new IllegalArgumentException("error  ingrese un usuario");
            }
            // verifica el código
            ResponseEntity<MessageDTO<State>> stateVerifiactionCode = manageUserClient.validateCode(code,messageDTO.getData().idUser());
            MessageDTO<State> stateUser = stateVerifiactionCode.getBody();
            if (stateUser == null || stateUser.getData() == null) {throw new NullPointerException("El usuario no puede ser nulo");}
            if (stateUser.getData() != State.SUCCESS) throw new IllegalArgumentException("El código no corresponde");

            //activa la cuenta (cambia el estado)
            ResponseEntity<MessageDTO<State>> stateActivation = manageUserClient.activateAccount(messageDTO.getData().idUser());
            MessageDTO<State> stateActive = stateActivation.getBody();
            if (stateActive == null || stateActive.getData() == null) {throw new NullPointerException("El valor no puede ser nulo");}
            if (stateActive.getData() != State.SUCCESS){
                throw new IllegalArgumentException("No se ha podido activar la cuenta");
            }
            return stateActive.getData();

        }catch (IllegalArgumentException | NullPointerException e) {
            return State.ERROR;

        }


    }

    @Override
    public State forgotPassword(String emailAddress)  {
        try {
            System.out.println(emailAddress);
            //Verificar si se ha enviado correctamente el correo electonico
            if (!StringUtils.hasText(emailAddress)) throw new IllegalArgumentException("error  ingrese un email");
            ResponseEntity<MessageDTO<ClientDTO>> user = manageUserClient.getUserByEmail(emailAddress);
            MessageDTO<ClientDTO> clientDTO = user.getBody();
            System.out.println(clientDTO.getData());
            if (clientDTO == null || clientDTO.getData() == null) {throw new NullPointerException("no puede ser nulo");}

            //generar el codigo para recuperar contraseña
            String codeForgotPassword = activationCodeGenerator.generateActivationCode();
            System.out.println(codeForgotPassword);

            //Enviar el codigo de verificación para ser guardado en la base de datos
            manageUserClient.saveCodeValidation(codeForgotPassword,clientDTO.getData().idUser());

            String emailContent =
                    "<!DOCTYPE html>\n" +
                            "<html lang=\"es\">\n" +
                            "<head>\n" +
                            "    <meta charset=\"UTF-8\">\n" +
                            "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                            "    <title>Bienvenido a Nuestra Aplicación</title>\n" +
                            "</head>\n" +
                            "<body style=\"font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 20px;\">\n" +
                            "    <table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"background-color: #f8f8f8; border-radius: 5px;\">\n" +
                            "        <tr>\n" +
                            "            <td style=\"padding: 20px; text-align: center;\">\n" +
                            "                <h1 style=\"color: #EDB017;\">Recuperar mi contraseña"  + "!</h1>\n" +
                            "<img src=\"" + logoUrl + "\" alt=\"Logo de Mi Boleta\" style=\"max-width: 100%; height: 100px; margin-bottom: 20px;\" />\n" +
                            "                <p style=\"font-size: 16px;\">"+ clientDTO.getData().name()+" Te hemos enviado el codigo para tu solicitud de recuperar tu contraseña</p>\n "+
                            "                <div style=\"background-color: #ffffff; border-radius: 5px; padding: 20px; margin: 20px 0;\">\n" +
                            "                    <p style=\"font-size: 18px; margin-bottom: 10px;\">Tu código de validación es:</p>\n" +
                            "                    <h2 style=\"color: #400101; font-size: 32px; letter-spacing: 5px; margin: 0;\">" + codeForgotPassword + "</h2>\n" + //
                            "                </div>\n" +
                            "                <p style=\"font-size: 16px;\">Por favor, utiliza este código para validar tu cuenta en nuestra aplicación.</p>\n" +
                            "                <p style=\"font-size: 14px; color: #666;\">Si no has solicitado esta cuenta, puedes ignorar este correo.</p>\n" +
                            "                <div style=\"margin-top: 30px; padding-top: 20px; border-top: 1px solid #ddd;\">\n" +
                            "                    <p style=\"font-size: 14px; color: #888;\">\n" +
                            "                        Si tienes alguna pregunta, no dudes en contactarnos en \n" +
                            "                        <a href=\"mailto:unilocalsoporte@gmail.com\" style=\"color: #4CAF50;\">unilocalsoporte@gmail.com</a>\n" +
                            "                    </p>\n" +
                            "                </div>\n" +
                            "            </td>\n" +
                            "        </tr>\n" +
                            "    </table>\n" +
                            "</body>\n" +
                            "</html>";

            //Enviar email con código
            mailService.sendMail(clientDTO.getData().emailAddress(),"Código de recuperación de contraseña", emailContent);
            return State.SUCCESS;


        } catch (NullPointerException | IllegalArgumentException e) {
            return State.ERROR;
        }
    }

    @Override
    public State verifyForgotPassword(String code, String emailAddress)  {
        try {
            //verificar que el codigo y el correo no vengan  no venga vacio
            if(!StringUtils.hasText(code) || !StringUtils.hasText(emailAddress)) throw new IllegalArgumentException("error  ingrese un código de verifiación o un correo valido");

            //obtener el cliente por su email
            ResponseEntity<MessageDTO<ClientDTO>> user = manageUserClient.getUserByEmail(emailAddress);
            MessageDTO<ClientDTO> messageDTO = user.getBody();

            //verificar el cliente se haya encontrado correctamente
            if (messageDTO == null || messageDTO.getData() == null) {
                throw new NullPointerException("No se ha encontrado el cliente");
            }
            // verifica el código
            ResponseEntity<MessageDTO<State>> messageVerifyCode = manageUserClient.validateCode(code, messageDTO.getData().idUser());
            MessageDTO<State> stateVerifyCode = messageVerifyCode.getBody();
            if (stateVerifyCode == null || stateVerifyCode.getData() == null) {throw new NullPointerException("no puede ser nulo");}

            if (stateVerifyCode.getData() != State.SUCCESS) throw new IllegalArgumentException("El código no corresponde");

            return stateVerifyCode.getData();

        }catch (IllegalArgumentException | NullPointerException e) {
            return State.ERROR;
        }
    }

    @Override
    public  TokenDTO changePassword(ChangePasswordDTO changePasswordDTO){

        String newPassword= changePasswordDTO.newPassword();
        String emailAddress = changePasswordDTO.emailAddress();

        try {
            //Verificar que la contraseña y el correo no vengan vacios
            if(!StringUtils.hasText(newPassword) || !StringUtils.hasText(emailAddress)){
                throw new IllegalArgumentException("la contraseña o el email no puede sre vació");
            }

            //obtener el usuario de la base de datos por medio del email
            ResponseEntity<MessageDTO<ClientDTO>> user  = manageUserClient.getUserByEmail(emailAddress);
            MessageDTO<ClientDTO> messageDTO = user.getBody();

            //verificar que se encontró el usuario
            if (messageDTO == null || messageDTO.getData() == null) {
                throw new NullPointerException("No se ha encontrado el cliente");
            }

            //verificar que el usuario ya haya verificado la cuenta
            if (messageDTO.getData().state() != State.ACTIVE) {
                throw  new IllegalArgumentException("no puede cambiar la contraseña de una cuenta inactiva");
            }
            //verificar que sea válida la contraseña con los parametros de seguiridad
            if (!validatePassword.validarContrasena(newPassword)) {
                throw new IllegalArgumentException("La contraseña no cumple con los parámetros especificados");
            }

            ClientDTO clientDTO = messageDTO.getData();
            //verificar que el cliente no sea nulo
            if (clientDTO == null) throw new IllegalArgumentException("La cliente no puede ser nulo");

            //actualizar la contraseña
            ResponseEntity<MessageDTO<State>> updatePassword =  manageUserClient.updatePassword(newPassword,emailAddress);
            MessageDTO<State> stateUpdatePassword = updatePassword.getBody();
            if (stateUpdatePassword == null || stateUpdatePassword.getData() == null) {
                throw new NullPointerException("El estado no puede ser nulo");
            }
            //verificar que el estado sea satisfactorio
            if (stateUpdatePassword.getData() != State.SUCCESS) {
                throw new IllegalArgumentException("No se pudo actualizar la contraseña ");
            }

            // Crear los atributos del token de autenticación
            Map<String, Object> authToken = new HashMap<>();
            authToken.put("role", clientDTO.role());
            authToken.put("name", clientDTO.name());
            authToken.put("idUser",clientDTO.idUser());

            //generar el token
            String token = jwtUtilsService.generarToken(clientDTO.emailAddress(), authToken);
            if (token == null) {
                throw new IllegalArgumentException("No se ha generado el token correctamente");
            }

            // Crear un objeto TokenDto para devolver el token generado
            return new TokenDTO(token);

        } catch (IllegalArgumentException| NullPointerException e) {
            return new TokenDTO(null);
        }
    }

    @Override
    public State verifyToken(String token) throws Exception {
        if (!StringUtils.hasText(token)) throw new IllegalArgumentException("por favor ingrese un token");

        return null;
    }

}
