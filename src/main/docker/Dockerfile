#
#
#  2016 (c) IxorTalk CVBA
#  All Rights Reserved.
#
# NOTICE:  All information contained herein is, and remains
# the property of IxorTalk CVBA
#
# The intellectual and technical concepts contained
# herein are proprietary to IxorTalk CVBA
# and may be covered by U.S. and Foreign Patents,
# patents in process, and are protected by trade secret or copyright law.
#
# Dissemination of this information or reproduction of this material
# is strictly forbidden unless prior written permission is obtained
# from IxorTalk CVBA.
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.
#

FROM frolvlad/alpine-oraclejdk8:slim
ADD ixortalk-gateway-*.jar /app.jar
ADD healthcheck.sh /
RUN sh -c 'touch /app.jar' && chmod +x /healthcheck.sh
HEALTHCHECK CMD /healthcheck.sh
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar /app.jar" ]
