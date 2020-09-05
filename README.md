# WatCourses

The backend for WatCourses, a website to help University of Waterloo students plan their degrees. It provides a RESTful API for frontend's use. The website is located at [watcourses.com](https://watcourses.com/).

Tech stack: Kotlin, Springboot, MySQL, Redis, protobuf

## How it works

### Sources of Information

| Information | Source | Automatic Scraping |
|-------------|--------|------|
|Course Information (description, requisites, etc.)| [Undergraduate Studies Academic Calender (e.g. Math)](http://www.ucalendar.uwaterloo.ca/2021/COURSE/course-MATH.html)  |  Y  |
|Course schedule (enrolment, instructor, etc.)|  [UWaterloo API](https://api.uwaterloo.ca/v2/courses/)     | Y |
|Like/Useful/Easy| [UWFlow](https://uwflow.com/) | Y  |
|Degree requirements |[Undergraduate Studies Academic Calender](https://ugradcalendar.uwaterloo.ca/) |  N  |

### Schedules validation/checking

Requisites in natural language are parsed to AST (boolean expression) in order to check whether a schedule conforms to the respective degree and course requirements. Complex requirements are manually parsed and imported.

Example:
```
Prereq: ACTSC 363, STAT 330, (one of STAT 331, 371, 373); Actuarial Science or Mathematical Finance students only
```
is parsed into the following AST:
```json
{"type":"AND","operands":[{"type":"OR","operands":[{"type":"HAS_LABEL","operands":[],"data":"Mathematical Finance"},{"type":"HAS_LABEL","operands":[],"data":"Actuarial Science"}],"data":null},{"type":"HAS_COURSE","operands":[],"data":"ACTSC 363"},{"type":"HAS_COURSE","operands":[],"data":"STAT 330"},{"type":"OR","operands":[{"type":"HAS_COURSE","operands":[],"data":"STAT 331"},{"type":"HAS_COURSE","operands":[],"data":"STAT 371"},{"type":"HAS_COURSE","operands":[],"data":"STAT 373"}],"data":null}],"data":null}
```
which translates into `([Mathematical Finance] || [Actuarial Science]) && ACTSC 363 && STAT 330 && (STAT 331 || STAT 371 || STAT 373)` (This boolean expression can also be parsed back into the AST).

Note that grades requirements are currently ignored because of privacy concerns (We do not collect or store your grades!)

Degree requirements are manually compiled for each degree. See [SE.toml](https://github.com/WatCoursePlanner/WatCourseBackend/blob/master/src/main/resources/degrees/SE.toml) for an example.

## Deployment/Development

You need to have MySQL (tested with MySQL 8.0) and Redis installed and change the configuration in `application.properties`.

Run `/admin/scraping/start-courses` to scrape courses from UWaterloo websites.

Note that access control for admin endpoints are not implemented yet; therefore, it is recommended that you block `/admin` from public access for now.

```nginx
location /admin/ { return 403; }
```

## License

This program is licensed under [AGPLv3](https://github.com/WatCoursePlanner/WatCourseBackend/blob/master/LICENSE).

