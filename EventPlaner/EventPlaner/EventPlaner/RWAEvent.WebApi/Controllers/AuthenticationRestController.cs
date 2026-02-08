using AutoMapper;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using RWAEvent.BL.Models.DTO.User;
using RWAEvent.BL.Models.Enums;
using RWAEvent.BL.Security;
using RWAEvent.BL.Services;

namespace RWAEvent.WebApi.Controllers
{
    [Route("api/auth")]
    [ApiController]
    public class AuthenticationController : ControllerBase
    {
        private const string ErrorUserExists = "Username {0} already exists";
        private const int JwtTokenDurationMinutes = 60;
        private const string ErrorPasswordsDoNotMatch = "Password and repeated password are not equal";
        private const string SuccessPasswordChanged = "Password changed successfully";

        private readonly IConfiguration _configuration;
        private readonly IUser _userService;
        private readonly IEventLogger _logger;
        private readonly IMapper _mapper;

        public AuthenticationController(IConfiguration configuration, IEventLogger logger, IUser userService, IMapper mapper)
        {
            _configuration = configuration;
            _logger = logger;
            _userService = userService;
            _mapper = mapper;
        }

        [HttpPost("register")]
        [AllowAnonymous]
        public async Task<ActionResult<int>> Registration(RegistrationDTO dto)
        {
            try
            {
                _logger.Log(LogLvl.Information, "Registration called with dto:" + dto.ToString());

                if (dto.Password != dto.RepeatPassword)
                    return BadRequest(ErrorPasswordsDoNotMatch);

                var trimmedUsername = dto.Username.Trim();
                var user = await _userService.FindUser(trimmedUsername);

                if (user != null)
                {
                    _logger.Log(LogLvl.Warning, "User already exist");
                    return BadRequest(string.Format(ErrorUserExists, trimmedUsername));
                }

                var userId = await _userService.CreateUser(dto);

                _logger.Log(LogLvl.Information, "User created with id:" + userId);
                return Ok("User created with id:" + userId);
            }
            catch (Exception e)
            {
                _logger.Log(LogLvl.Error, "Error while creating user");
                return StatusCode(StatusCodes.Status500InternalServerError, e.Message);
            }
        }

        [HttpPost("login")]
        [AllowAnonymous]
        public async Task<IActionResult> Login(LoginDTO dto)
        {
            try
            {
                _logger.Log(LogLvl.Information, "Login called");

                var user = await _userService.Login(dto.Username, dto.Password);

                var secureKey = _configuration["JWT:SecureKey"];

                var token = JwtTokenProvider.CreateToken(
                    secureKey,
                    JwtTokenDurationMinutes,
                    user.Username,
                    user.Role.ToString()
                );

                return Ok(token);
            }
            catch (InvalidOperationException e)
            {
                return BadRequest(e.Message);
            }
            catch (Exception ex)
            {
                _logger.Log(LogLvl.Error, "Error on login");
                return StatusCode(500, ex.Message);
            }
        }

        [HttpPost("changepassword")]
        [Authorize(AuthenticationSchemes = JwtBearerDefaults.AuthenticationScheme)]
        public async Task<ActionResult> ChangePassword(ChangePasswordDTO dto)
        {
            try
            {
                var updated = await _userService.ChangePassword(dto);

                if (!updated)
                {
                    return BadRequest("Password change failed");
                }

                return Ok(SuccessPasswordChanged);
            }
            catch (InvalidOperationException e)
            {
                return BadRequest(e.Message);
            }
            catch (Exception e)
            {
                return StatusCode(StatusCodes.Status500InternalServerError, e.Message);
            }
        }


        [HttpGet("{username}")]
        [Authorize(AuthenticationSchemes = JwtBearerDefaults.AuthenticationScheme)]
        public async Task<IActionResult> GetUser(string username)
        {
            try
            {
                _logger.Log(LogLvl.Information, "Get user for username:" + username);

                var user = await _userService.GetUserByUsername(username);
                if (user == null)
                {
                    _logger.Log(LogLvl.Warning, "User not found for username:" + username);
                    return NotFound();
                }

                var prevUser = _mapper.Map<UserPreviewDTO>(user);
                _logger.Log(LogLvl.Information, "User found:" + prevUser);
                return Ok(prevUser);
            }
            catch (Exception e)
            {
                _logger.Log(LogLvl.Error, $"Error retrieving user for: {username}");
                return StatusCode(StatusCodes.Status500InternalServerError, e.Message);
            }
        }

        [ApiExplorerSettings(IgnoreApi = true)]
        [HttpGet("preview")]
        public IActionResult LoginPreview()
        {
            return Redirect("/pages/Login.html");
        }
    }
}
