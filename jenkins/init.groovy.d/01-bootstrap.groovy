import hudson.plugins.git.BranchSpec
import hudson.plugins.git.GitSCM
import hudson.plugins.git.UserRemoteConfig
import hudson.security.FullControlOnceLoggedInAuthorizationStrategy
import hudson.security.HudsonPrivateSecurityRealm
import jenkins.model.Jenkins
import org.jenkinsci.plugins.workflow.cps.CpsScmFlowDefinition
import org.jenkinsci.plugins.workflow.job.WorkflowJob

def instance = Jenkins.instance
def adminId = System.getenv("JENKINS_ADMIN_ID") ?: "admin"
def adminPassword = System.getenv("JENKINS_ADMIN_PASSWORD") ?: "admin"

if (instance.getSecurityRealm() == null || !(instance.getSecurityRealm() instanceof HudsonPrivateSecurityRealm)) {
    def realm = new HudsonPrivateSecurityRealm(false)
    realm.createAccount(adminId, adminPassword)
    instance.setSecurityRealm(realm)
    def strategy = new FullControlOnceLoggedInAuthorizationStrategy()
    strategy.setAllowAnonymousRead(false)
    instance.setAuthorizationStrategy(strategy)
    instance.save()
}

def repoUrl = System.getenv("GIT_REPO_URL")
if (!repoUrl) {
    throw new IllegalStateException("GIT_REPO_URL não definido")
}

if (repoUrl.startsWith("file:")) {
    GitSCM.ALLOW_LOCAL_CHECKOUT = true
}

def jobName = "desafio-devops-cd"
def job = instance.getItem(jobName)
if (job == null) {
    def scm = new GitSCM(
        [new UserRemoteConfig(repoUrl, null, null, null)],
        [new BranchSpec("*/main")],
        null,
        null,
        Collections.emptyList()
    )
    def definition = new CpsScmFlowDefinition(scm, "Jenkinsfile")
    definition.setLightweight(true)
    job = instance.createProject(WorkflowJob, jobName)
    job.definition = definition
    job.description = "Entrega contínua da página HTML: checkout no GitHub, build da imagem Nginx e deploy do container."
    job.save()
}
