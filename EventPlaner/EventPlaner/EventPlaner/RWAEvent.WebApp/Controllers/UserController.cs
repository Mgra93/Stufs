using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using RWAEvent.BL.Models.DTO.User;
using RWAEvent.BL.Models.Filters;
using RWAEvent.BL.Services;
using RWAEvent.WebApp.ViewsModel;

namespace RWAEvent.WebApp.Controllers
{
    public class UserController : Controller
    {
        private readonly IUser _userService;

        public UserController(IUser userService)
        {
            _userService = userService;
        }

        [HttpGet]
        [Authorize]
        public async Task<IActionResult> List(string? search, int page = 1, int pageSize = 10)
        {
            var filter = new UserFilter
            {
                Search = search,
                Page = page,
                PageSize = pageSize
            };

            var userList = await _userService.GetUserByFilter(filter);
            var totalCount = userList.Count;

            var model = new UserVM
            {
                Users = userList,
                CurrentPage = page,
                TotalPages = (int)Math.Ceiling((double)totalCount / pageSize),
                Filter = filter
            };

            return View(model);
        }

        [HttpPost]
        [Authorize]
        public IActionResult List(ReservationVM viewModel)
        {
            var filter = viewModel.Filter ?? new ReservationFilter();

            return RedirectToAction("List", new
            {
                page = 1,
                pageSize = filter.PageSize,
                search = filter.Search
            });
        }

        public IActionResult Detail()
        {
            return View();
        }

        [HttpGet]
        [Authorize]
        public async Task<IActionResult> Data(string username)
        {
            UserDTO userDto = await _userService.GetUserByUsername(username);
            if (userDto == null)
                return NotFound();

            return Json(userDto);
        }

        [HttpPost]
        [Authorize]
        public async Task<IActionResult> Update([FromBody] UserDTO dto)
        {
            try
            {
                if (!ModelState.IsValid)
                {
                    return BadRequest(ModelState);
                }

                bool success = await _userService.UpdateUser(dto);

                if (!success)
                {
                    return NotFound("User not found");
                }

                return Ok();

            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }
        }
    }
}
